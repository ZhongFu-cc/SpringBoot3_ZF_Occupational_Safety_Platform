package tw.com.zf_occupational_safety_platform.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.controller.DepartmentController.AddDepartmentCourse;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.DepartmentException;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 部門 - 管理層
 */
@Component
@RequiredArgsConstructor
public class DepartmentManager {

	private final SysUserService sysUserService;
	private final DepartmentService departmentService;
	private final DepartmentCourseService departmentCourseService;
	private final CompanyCourseService companyCourseService;
	private final CourseEnrollmentService courseEnrollmentService;
	private final CourseService courseService;
	private final CourseChapterService courseChapterService;
	private final ChapterProgressService chapterProgressService;

	private record EnrollmentKey(Long userId, Long courseId) {
	}

	/**
	 * 拿到操作者，所能操作的部門 單一對象
	 * 
	 * @param departmentId
	 * @param operator
	 * @return
	 */
	public Department getDepartment(Long departmentId, SysUserVO operator) {
		return departmentService.getByIdAndCompany(departmentId, operator.getCompanyId());
	}

	/**
	 * 拿到操作者，所能操作的部門 分頁對象
	 * 
	 * @param pageInfo  分頁資訊
	 * @param queryText 查詢條件
	 * @param operator  操作者
	 * @return
	 */
	public IPage<Department> findDepartmentPage(Page<Department> pageInfo, String queryText, SysUserVO operator) {
		return departmentService.findPageByQuery(pageInfo, queryText, operator.getCompanyId());
	}

	/**
	 * 創建部門，companyId 以當前操作者為主
	 * 
	 * @param addDepartmentDTO
	 * @param operator
	 */
	public void createDepartment(AddDepartmentDTO addDepartmentDTO, SysUserVO operator) {
		addDepartmentDTO.setCompanyId(operator.getCompanyId());
		departmentService.create(addDepartmentDTO);
	}

	/**
	 * 修改部門，companyId 已當前操作者為主
	 * 
	 * @param putDepartmentDTO
	 * @param operator
	 */
	public void updateDepartment(PutDepartmentDTO putDepartmentDTO, SysUserVO operator) {

		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(putDepartmentDTO.getDepartmentId());

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		departmentService.update(putDepartmentDTO);
	}

	/**
	 * 移除部門
	 * 
	 * @param departmentId
	 * @param operator
	 */
	public void removeDepartment(Long departmentId, SysUserVO operator) {

		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(departmentId);

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 先判斷employee , 都有更改成其他部門了才往下進行操作
		long countByDepartment = sysUserService.countByDepartment(departmentId);
		if (countByDepartment != 0) {
			throw new DepartmentException("請將部門人員清空，再進行刪除操作");
		}

		// 移除部門 x 課程的關聯
		departmentCourseService.removeByDepartment(departmentId);

		// 移除部門本身
		departmentService.remove(departmentId);
	}

	/**
	 * 分配/移除 部門x課程
	 * 
	 * @param addDepartmentCourse
	 * @param operator
	 */
	public void assignCourse2Department(AddDepartmentCourse addDepartmentCourse, SysUserVO operator) {
		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(addDepartmentCourse.departmentId());

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 根據公司ID 和 課程ID去查詢是否屬於企業的課程
		List<CompanyCourse> companyCourses = companyCourseService
				.findByIdsAndCompany(addDepartmentCourse.companyCourseIds(), operator.getCompanyId());
		if (companyCourses.isEmpty()) {
			return;
		}

		// 提取 企業課程ID 去重後進行分配
		Set<Long> companyCourseIds = companyCourses.stream()
				.map(CompanyCourse::getCompanyCourseId)
				.collect(Collectors.toSet());
		departmentCourseService.assignCourse2Department(addDepartmentCourse.departmentId(), companyCourseIds);

	}

	/**
	 * 為企業所有部門員工，報名應上的課程
	 * 
	 * @param operator
	 */
	@Transactional
	public void oneClickEnrollment(SysUserVO operator) {

		// 1. 查詢所有部門
		List<Department> departments = departmentService.findByCompany(operator.getCompanyId());

		if (departments.isEmpty()) {
			return;
		}

		List<Long> departmentIds = departments.stream().map(Department::getDepartmentId).toList();

		// 2. 查詢部門所有員工
		List<SysUser> employees = sysUserService.findByDepartments(departmentIds);

		if (employees.isEmpty()) {
			return;
		}

		// 3. 部門 -> companyCourseIds
		Map<Long, List<Long>> departmentCompanyCourseMap = departmentCourseService.mapByDepartmentId(departmentIds);
		if (departmentCompanyCourseMap.isEmpty()) {
			return;
		}

		// 4. 收集所有 companyCourseId
		List<Long> companyCourseIds = departmentCompanyCourseMap.values()
				.stream()
				.flatMap(List::stream)
				.distinct()
				.toList();

		// 5. companyCourse -> course
		List<CompanyCourse> companyCourses = companyCourseService.findByIds(companyCourseIds);

		Map<Long, Long> companyCourseToCourseMap = companyCourses.stream()
				.collect(Collectors.toMap(CompanyCourse::getCompanyCourseId, CompanyCourse::getCourseId));

		// 6. department -> courseIds
		Map<Long, List<Long>> departmentCourseMap = departmentCompanyCourseMap.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						entry -> entry.getValue()
								.stream()
								.map(companyCourseToCourseMap::get)
								.filter(Objects::nonNull)
								.distinct()
								.toList()));

		// 7. 所有課程ID
		List<Long> allCourseIds = departmentCourseMap.values().stream().flatMap(List::stream).distinct().toList();

		if (allCourseIds.isEmpty()) {
			return;
		}

		// 8. 一次查所有課程
		Map<Long, Course> courseMap = courseService.findByIds(allCourseIds)
				.stream()
				.collect(Collectors.toMap(Course::getCourseId, Function.identity()));

		// 9. 一次查所有非目錄章節
		List<CourseChapter> chapters = courseChapterService.findNonDirectoryByCourseIds(allCourseIds);

		// courseId -> chapter list
		Map<Long, List<CourseChapter>> chapterMap = chapters.stream()
				.collect(Collectors.groupingBy(CourseChapter::getCourseId));

		// courseId -> chapter count
		Map<Long, Integer> chapterCountMap = chapters.stream()
				.collect(Collectors.groupingBy(CourseChapter::getCourseId,
						Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

		// 10. 查詢已報名
		List<Long> userIds = employees.stream().map(SysUser::getSysUserId).toList();

		List<CourseEnrollment> existedEnrollments = courseEnrollmentService.findByUsersAndCourses(userIds,
				allCourseIds);

		Set<EnrollmentKey> enrolledSet = existedEnrollments.stream()
				// 未開始、執行中、已完成 這類的是已經有報名並準備上課的就不用重複新增
				// 所以他們是重複的,要進行保留,方便後續跳過
				.filter(e -> Set
						.of(CourseStatusEnum.NOT_STARTED, CourseStatusEnum.IN_PROGRESS, CourseStatusEnum.COMPLETED)
						.contains(e.getStatus()))
				.map(e -> new EnrollmentKey(e.getSysUserId(), e.getCourseId()))
				.collect(Collectors.toSet());

		List<CourseEnrollment> enrollments = new ArrayList<>();
		List<ChapterProgress> progresses = new ArrayList<>();

		LocalDateTime now = LocalDateTime.now();

		// 11. 建立 Enrollment 與 Progress
		for (SysUser employee : employees) {

			List<Long> requiredCourses = departmentCourseMap.getOrDefault(employee.getDepartmentId(),
					Collections.emptyList());

			for (Long courseId : requiredCourses) {

				EnrollmentKey key = new EnrollmentKey(employee.getSysUserId(), courseId);

				if (enrolledSet.contains(key)) {
					continue;
				}

				Course course = courseMap.get(courseId);

				if (course == null) {
					continue;
				}

				// Snowflake ID
				Long enrollmentId = IdWorker.getId();

				CourseEnrollment enrollment = new CourseEnrollment();
				enrollment.setCourseEnrollmentId(enrollmentId);
				enrollment.setCompanyId(operator.getCompanyId());
				enrollment.setDepartmentId(employee.getDepartmentId());
				enrollment.setSysUserId(employee.getSysUserId());
				enrollment.setCourseId(courseId);
				enrollment.setStatus(CourseStatusEnum.NOT_STARTED);
				enrollment.setCompletedChapters(0);
				enrollment.setIsChaptersDone(CommonStatusEnum.NO);
				enrollment.setIsMinutesMet(CommonStatusEnum.NO);
				enrollment.setEnrolledAt(now);
				enrollment.setTotalChapters(chapterCountMap.getOrDefault(courseId, 0));
				enrollment.setRequiredSeconds(course.getTotalMinutes() * 60);
				enrollment.setCreateBy(operator.getAccount());

				enrollments.add(enrollment);

				// 建立章節進度
				List<CourseChapter> courseChapters = chapterMap.getOrDefault(courseId, Collections.emptyList());

				for (CourseChapter chapter : courseChapters) {

					ChapterProgress progress = new ChapterProgress();

					progress.setCourseEnrollmentId(enrollmentId);
					progress.setCourseChapterId(chapter.getCourseChapterId());
					progress.setCourseId(chapter.getCourseId());
					progress.setSysUserId(employee.getSysUserId());
					progress.setStatus(CourseStatusEnum.NOT_STARTED);

					// 如果當前章節的類別為測驗
					if (ChapterContentTypeEnum.QUIZ.equals(chapter.getContentType())) {
						progress.setIsQuizPassed(CommonStatusEnum.NO);
					} else {
						// 如果不是測驗，直接當作測驗通過
						progress.setIsQuizPassed(CommonStatusEnum.YES);
						progress.setQuizScore(100);
					}

					progresses.add(progress);
				}
			}
		}

		// 12. 批次新增
		if (!enrollments.isEmpty()) {
			courseEnrollmentService.saveBatch(enrollments);
		}

		if (!progresses.isEmpty()) {
			chapterProgressService.saveBatch(progresses);
		}
	}

}
