package tw.com.zf_occupational_safety_platform.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.convert.CourseEnrollmentConvert;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseEnrollmentVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningRecordVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeStudyHistoryExcel;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
import tw.com.zf_occupational_safety_platform.service.CourseCategoryService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

/**
 * 課程報名 - 管理層
 */
@Component
@RequiredArgsConstructor
public class CourseEnrollmentManager {

	private final CourseService courseService;
	private final CourseConvert courseConvert;
	private final CourseCategoryService courseCategoryService;
	private final CourseChapterService courseChapterService;
	private final CourseEnrollmentService courseEnrollmentService;
	private final CourseEnrollmentConvert courseEnrollmentConvert;
	private final ChapterProgressService chapterProgressService;
	private final ChapterWatchLogService chapterWatchLogService;

	public CourseEnrollmentVO getByOwner(Long courseEnrollmentId) {

		Map<Long, Course> mapByCourseId = courseService.findCourseIdMapByQuery(null);

		CourseEnrollment courseEnrollment = courseEnrollmentService.get(courseEnrollmentId);
		CourseEnrollmentVO vo = courseEnrollmentConvert.entityToVO(courseEnrollment);
		Course course = mapByCourseId.get(courseEnrollment.getCourseId());

		vo.setCourseCategoryId(course.getCourseCategoryId());
		vo.setCourseName(course.getTitle());
		vo.setCourseCoverImage(course.getCoverImage());
		vo.setCourseDescription(course.getDescription());

		return vo;
	}

	/**
	 * 查詢 已報名課程 分頁對象
	 * 
	 * @param pageInfo         分頁對象
	 * @param courseStatusEnum 課程學習狀態
	 * @param operator         操作者
	 * @return
	 */
	public IPage<CourseEnrollmentVO> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum courseStatusEnum,
			SysUserVO operator) {
		IPage<CourseEnrollment> courseEnrollmentPage = courseEnrollmentService.findPageByOwner(pageInfo,
				courseStatusEnum, operator.getSysUserId());

		Map<Long, Course> mapByCourseId = courseService.findCourseIdMapByQuery(null);

		List<CourseEnrollmentVO> vos = courseEnrollmentPage.getRecords().stream().map(courseEnrollment -> {
			Course course = mapByCourseId.get(courseEnrollment.getCourseId());

			CourseEnrollmentVO vo = courseEnrollmentConvert.entityToVO(courseEnrollment);
			vo.setCourseCategoryId(course.getCourseCategoryId());
			vo.setCourseName(course.getTitle());
			vo.setCourseCoverImage(course.getCoverImage());
			vo.setCourseDescription(course.getDescription());
			return vo;
		}).toList();

		Page<CourseEnrollmentVO> voPage = new Page<CourseEnrollmentVO>(courseEnrollmentPage.getCurrent(),
				courseEnrollmentPage.getSize(), courseEnrollmentPage.getTotal());
		voPage.setRecords(vos);
		return voPage;

	}

	/**
	 * 用戶(企業員工)查詢自身學習歷程
	 * 
	 * @param pageInfo
	 * @param courseStatusEnum
	 * @param queryText
	 * @param operator
	 * @return
	 */
	public IPage<LearningRecordVO> findLearningRecordByOwner(Page<CourseEnrollment> pageInfo,
			CourseStatusEnum courseStatusEnum, String queryText, SysUserVO operator) {
		// 在課程模糊查詢的條件下，拿到Map對象
		Map<Long, Course> mapByCourseId = courseService.findCourseIdMapByQuery(queryText);
		// 拿著查詢條件拿到分頁對象
		IPage<CourseEnrollment> courseEnrollmentPage = courseEnrollmentService.findPageByOwner(pageInfo,
				courseStatusEnum, operator.getSysUserId(), mapByCourseId.keySet());

		// stream處理並進行轉換
		List<LearningRecordVO> vos = courseEnrollmentPage.getRecords().stream().map(courseEnrollment -> {
			Course course = mapByCourseId.get(courseEnrollment.getCourseId());

			LearningRecordVO vo = courseEnrollmentConvert.entityToLearningRecord(courseEnrollment);
			vo.setCourseName(course.getTitle());

			// 設定章節完成率
			BigDecimal rate;
			if (new BigDecimal(courseEnrollment.getTotalChapters()).compareTo(BigDecimal.ZERO) == 0) {
				rate = BigDecimal.ZERO;
			} else {
				rate = new BigDecimal(courseEnrollment.getCompletedChapters())
						.divide(new BigDecimal(courseEnrollment.getTotalChapters()), 4, RoundingMode.HALF_UP)
						.multiply(new BigDecimal(100))
						.setScale(1, RoundingMode.HALF_UP);
			}

			vo.setProgress(rate.toString() + "%");

			if (courseEnrollment.getCompletedAt() != null) {
				vo.setIsCompleted(CommonStatusEnum.YES);
			} else {
				vo.setIsCompleted(CommonStatusEnum.NO);
			}

			return vo;
		}).toList();

		Page<LearningRecordVO> voPage = new Page<LearningRecordVO>(courseEnrollmentPage.getCurrent(),
				courseEnrollmentPage.getSize(), courseEnrollmentPage.getTotal());
		voPage.setRecords(vos);
		return voPage;
	}

	/**
	 * 用戶(企業員工) 報名 課程
	 * 
	 * @param courseId 課程ID
	 * @param operator 報名者(操作者)
	 */
	public void enrollCourse(Long courseId, SysUserVO operator) {

		// 1.獲得一個初始化的 報名資訊
		CourseEnrollment courseEnrollment = courseEnrollmentService.create(operator.getSysUserId(), courseId);

		// 2.拿到非Directory類別的 課程章節
		List<CourseChapter> courseChapters = courseChapterService.findNonDirectoryByCourseId(courseId);

		// 3.拿到課程
		Course course = courseService.get(courseId);

		// 4.填充其他資訊
		courseEnrollment.setTotalChapters(courseChapters.size());
		courseEnrollment.setRequiredSeconds(course.getTotalMinutes() * 60);

		// 5.更新報名資訊
		courseEnrollmentService.saveOrUpdate(courseEnrollment);

		// 6.創建報名此課程後，所有課程章節的追蹤進度
		chapterProgressService.batchCreateByCourseChapter(courseEnrollment.getCourseEnrollmentId(),
				operator.getSysUserId(), courseChapters);

	}

	/**
	 * 
	 * 取消報名 課程
	 * 
	 * @param courseEnrollmentId 報名ID
	 * @param operator           報名者(操作者)
	 */
	public void cancelEnrollment(Long courseEnrollmentId, SysUserVO operator) {

		// 將報名資訊查出
		CourseEnrollment courseEnrollment = courseEnrollmentService.get(courseEnrollmentId);

		if (!courseEnrollment.getSysUserId().equals(operator.getSysUserId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 刪除課程章節進度
		chapterProgressService.removeByEnrollmentId(courseEnrollmentId);

		// 刪除課程觀看明細
		chapterWatchLogService.removeByEnrollmentId(courseEnrollmentId);

		// 最後刪除報名本身
		courseEnrollmentService.remove(courseEnrollmentId);
	}

	/**
	 * 下載 個人 學習歷程
	 * 
	 * @param response
	 * @param operator
	 * @throws IOException
	 */
	public void downloadStudyHistory(HttpServletResponse response, SysUserVO operator) throws IOException {
		// 1.設置Excel 檔案資訊
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		// 这里URLEncoder.encode可以防止中文乱码 ， 和easyexcel没有关系
		String fileName = URLEncoder.encode("學習歷程", "UTF-8").replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");

		// 2.獲取用戶所有報名的課程
		List<CourseEnrollment> courseEnrollments = courseEnrollmentService.findBySysUser(operator.getSysUserId());

		// 3.抽取課程Id,查詢有上的課程
		Set<Long> courseIds = courseEnrollments.stream().map(CourseEnrollment::getCourseId).collect(Collectors.toSet());
		List<Course> courses = courseService.findByIds(courseIds);
		// 製成 map 對象
		Map<Long, Course> courseMap = courses.stream()
				.collect(Collectors.toMap(Course::getCourseId, Function.identity()));

		// 4.獲取課程類別的映射對象
		Map<Long, CourseCategory> courseCategoryMap = courseCategoryService.mapById();

		// 5.轉換
		List<EmployeeStudyHistoryExcel> excelData = courseEnrollments.stream().map(courseEnrollment -> {
			// 拿到課程
			Course course = courseMap.get(courseEnrollment.getCourseId());
			// 拿到課程類別
			CourseCategory courseCategory = courseCategoryMap.get(course.getCourseCategoryId());
			// pojo轉換
			EmployeeStudyHistoryExcel excelRow = courseConvert.toEmployeeStudyHistoryExcel(course, courseEnrollment);
			// 補上課程類別名稱、用戶名
			excelRow.setCourseCategoryName(courseCategory.getName());
			excelRow.setUserName(operator.getRealName());

			return excelRow;
		}).toList();

		// 6.輸出成Excel
		EasyExcel.write(response.getOutputStream(), EmployeeStudyHistoryExcel.class).sheet("學習歷程").doWrite(excelData);

	}

}
