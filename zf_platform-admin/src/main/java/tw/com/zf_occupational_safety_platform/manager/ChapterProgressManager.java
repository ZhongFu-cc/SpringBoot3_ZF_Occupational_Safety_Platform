package tw.com.zf_occupational_safety_platform.manager;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

/**
 * 章節學習進度 - 管理層
 */
@Component
@RequiredArgsConstructor
public class ChapterProgressManager {

	private final CourseEnrollmentService courseEnrollmentService;
	private final ChapterProgressService chapterProgressService;
	private final ChapterWatchLogService chapterWatchLogService;

	/**
	 * 獲取單一 章節學習進度
	 * 
	 * @param id       主鍵ID
	 * @param operator 操作者
	 * @return
	 */
	public ChapterProgress getProgress(Long id, SysUserVO operator) {
		return chapterProgressService.getByOwner(id, operator.getSysUserId());
	}

	/**
	 * 獲取當前用戶 章節學習進度
	 * 
	 * @param courseEnrollmentId 報名ID
	 * @param courseChapterId    章節ID
	 * @param operator           操作者
	 * @return
	 */
	public ChapterProgress getUserChapterProgress(Long courseEnrollmentId, Long courseChapterId, SysUserVO operator) {
		return chapterProgressService.getByEnrollmentAndChapter(courseEnrollmentId, courseChapterId,
				operator.getSysUserId());
	}
	
	

	/**
	 * 學習章節
	 * 
	 * @param chapterProgressId
	 * @param sysUserVO
	 * @return
	 */
	public ChapterWatchLog learningChaprer(Long chapterProgressId, SysUserVO sysUserVO) {

		// 拿到 章節學習進度
		ChapterProgress chapterProgress = chapterProgressService.getByOwner(chapterProgressId,
				sysUserVO.getSysUserId());

		// 拿到 報名課程資訊
		CourseEnrollment courseEnrollment = courseEnrollmentService.get(chapterProgress.getCourseEnrollmentId());

		// 章節觀看次數 + 1 
		chapterProgress.setWatchCount(chapterProgress.getWatchCount() + 1);

		// 當今天訪問非測驗型章節，直接代表他學習完了
		if (chapterProgress.getIsQuizPassed().getBooleanValue()) {
			LocalDateTime now = LocalDateTime.now();
			chapterProgress.setCompletedAt(now);
			chapterProgress.setStatus(CourseStatusEnum.COMPLETED);

			// 同時更新 課程完成的整體狀態
			Integer totalChapters = courseEnrollment.getTotalChapters();
			Integer completedChapters = courseEnrollment.getCompletedChapters();
			completedChapters += 1;

			// 當完成課程章節數 大於等於 總共課程章節數
			if (completedChapters >= totalChapters) {
				courseEnrollment.setCompletedChapters(totalChapters);
				courseEnrollment.setIsChaptersDone(CommonStatusEnum.YES);
			} else {
				// 當完成課程章節數 大於等於 總共課程章節數
				courseEnrollment.setCompletedChapters(completedChapters);
			}
			courseEnrollmentService.updateById(courseEnrollment);

		}

		else {
			// 測驗型章節,則先處理進行中,等問卷回答完成,回調時在更新狀態
			chapterProgress.setStatus(CourseStatusEnum.IN_PROGRESS);
		}

		// 章節學習進度更新
		chapterProgressService.updateById(chapterProgress);

		// 此時創建一個 觀看紀錄
		ChapterWatchLog chapterWatchLog = chapterWatchLogService.create(chapterProgressId,
				chapterProgress.getCourseEnrollmentId(), sysUserVO.getSysUserId(),
				chapterProgress.getCourseChapterId());

		// 回傳觀看紀錄,讓前端拿到紀錄ID , 定期call API 維持心跳包
		return chapterWatchLog;

	};

}
