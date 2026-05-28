package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
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
	private final CourseChapterService courseChapterService;
	private final CourseEnrollmentService courseEnrollmentService;
	private final ChapterProgressService chapterProgressService;
	private final ChapterWatchLogService chapterWatchLogService;

	/**
	 * 報名 課程
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

}
