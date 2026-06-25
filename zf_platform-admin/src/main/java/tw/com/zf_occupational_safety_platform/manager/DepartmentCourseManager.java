package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.controller.DepartmentCourseController.AddDepartmentCourse;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.convert.DepartmentCourseConvert;
import tw.com.zf_occupational_safety_platform.exception.DepartmentException;
import tw.com.zf_occupational_safety_platform.pojo.VO.DepartmentCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

@Component
@RequiredArgsConstructor
public class DepartmentCourseManager {

	private final DepartmentCourseService departmentCourseService;
	private final DepartmentCourseConvert departmentCourseConvert;
	private final CompanyCourseService companyCourseService;
	private final CourseService courseService;

	/**
	 * 查詢部門持有課程 - 列表對象
	 * 
	 * @param departmentId
	 * @param queryText
	 * @param sysUserVO
	 * @return
	 */
	public List<DepartmentCourseVO> findDepartmentCourseList(Long departmentId, String queryText, SysUserVO sysUserVO) {

		List<Course> courses = courseService.findByQuery(queryText);
		List<Long> courseIds = courses.stream().map(Course::getCourseId).toList();

		// 課程ID 與 資訊映射
		Map<Long, Course> mapByCourseId = courseService.findCourseIdMapByQuery(queryText);

		// 查出符合條件的企業課程
		List<CompanyCourse> companyCourses = companyCourseService.findByCompanyIdAndCourseIds(sysUserVO.getCompanyId(),
				courseIds);

		// 建立 companyCourseId -> courseId 的映射關係
		Map<Long, Long> companyCourseToCourseMap = companyCourses.stream()
				.collect(Collectors.toMap(CompanyCourse::getCompanyCourseId, CompanyCourse::getCourseId));

		// 抽取 符合條件的企業課程 ID
		Set<Long> companyCourseIds = companyCourses.stream()
				.map(CompanyCourse::getCompanyCourseId)
				.collect(Collectors.toSet());

		List<DepartmentCourse> departmentCourses = departmentCourseService.findByCompanyCourses(departmentId,
				companyCourseIds);

		List<DepartmentCourseVO> vos = departmentCourses.stream().map(departmentCourse -> {
			Long courseId = companyCourseToCourseMap.get(departmentCourse.getCompanyCourseId());
			Course course = mapByCourseId.get(courseId);

			DepartmentCourseVO vo = departmentCourseConvert.entityToDepartmentCourseVO(departmentCourse);
			vo.setCourseCategoryId(course.getCourseCategoryId());
			vo.setCourseName(course.getTitle());
			vo.setCourseCoverImage(course.getCoverImage());
			vo.setCourseDescription(course.getDescription());
			vo.setCourseTotalMinutes(course.getTotalMinutes());

			return vo;
		}).toList();

		return vos;
	}

	/**
	 * 查詢部門持有課程 - 分頁對象
	 * 
	 * @param pageInfo     分頁資訊
	 * @param departmentId 部門ID
	 * @param queryText    查詢條件
	 * @param sysUserVO    操作者
	 * @return
	 */
	public IPage<DepartmentCourseVO> findDepartmentCoursePage(Page<DepartmentCourse> pageInfo, Long departmentId,
			String queryText, SysUserVO sysUserVO) {

		List<Course> courses = courseService.findByQuery(queryText);
		List<Long> courseIds = courses.stream().map(Course::getCourseId).toList();

		// 課程ID 與 資訊映射
		Map<Long, Course> mapByCourseId = courseService.findCourseIdMapByQuery(queryText);

		// 查出符合條件的企業課程
		List<CompanyCourse> companyCourses = companyCourseService.findByCompanyIdAndCourseIds(sysUserVO.getCompanyId(),
				courseIds);

		// 建立 companyCourseId -> courseId 的映射關係
		Map<Long, Long> companyCourseToCourseMap = companyCourses.stream()
				.collect(Collectors.toMap(CompanyCourse::getCompanyCourseId, CompanyCourse::getCourseId));

		Set<Long> companyCourseIds = companyCourses.stream()
				.map(CompanyCourse::getCompanyCourseId)
				.collect(Collectors.toSet());

		IPage<DepartmentCourse> departmentCourses = departmentCourseService.findPage(pageInfo, departmentId,
				companyCourseIds);

		List<DepartmentCourseVO> vos = departmentCourses.getRecords().stream().map(departmentCourse -> {
			Long courseId = companyCourseToCourseMap.get(departmentCourse.getCompanyCourseId());
			Course course = mapByCourseId.get(courseId);

			DepartmentCourseVO vo = departmentCourseConvert.entityToDepartmentCourseVO(departmentCourse);
			vo.setCourseCategoryId(course.getCourseCategoryId());
			vo.setCourseName(course.getTitle());
			vo.setCourseCoverImage(course.getCoverImage());
			vo.setCourseDescription(course.getDescription());
			vo.setCourseTotalMinutes(course.getTotalMinutes());

			return vo;
		}).toList();

		Page<DepartmentCourseVO> voPage = new Page<DepartmentCourseVO>(departmentCourses.getCurrent(),
				departmentCourses.getSize(), departmentCourses.getTotal());
		voPage.setRecords(vos);
		return voPage;

	}

	/**
	 * 為部門新增課程，新增前會校驗是否存在，不存在則拋出異常
	 * 
	 * @param addDepartmentCourse
	 * @param sysUserVO
	 */
	public void addCourse2Department(AddDepartmentCourse addDepartmentCourse, SysUserVO sysUserVO) {
		departmentCourseService.addCourse2Department(addDepartmentCourse.departmentId(),
				addDepartmentCourse.companyCourseId());

	}

	/**
	 * 根據主鍵ID，刪除部門課程
	 * 
	 * @param departmentCourseId
	 * @param operator
	 */
	public void removeByDepartmentCourseId(Long departmentCourseId) {
		departmentCourseService.removeById(departmentCourseId);
	}

}
