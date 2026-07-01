package tw.com.zf_occupational_safety_platform.manager;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.ChapterWatchLogException;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.TreeUtil;

/**
 * 章節學習進度 - 管理層
 */
@Component
@RequiredArgsConstructor
public class ChapterProgressManager {

	private final CourseEnrollmentService courseEnrollmentService;
	private final ChapterProgressService chapterProgressService;
	private final CourseChapterService courseChapterService;
	private final ChapterWatchLogService chapterWatchLogService;
	private final RedissonClient redissonClient;

	/** last_beat TTL = 心跳間隔 3 倍，容許偶爾一次網路抖動 */
	private static final Duration LAST_BEAT_TTL = Duration.ofSeconds(90L);
	/** duration 保留 24 小時，確保 last_beat 過期事件觸發時還能讀到 */
	private static final Duration DURATION_TTL = Duration.ofHours(24L);

	/** duration RMap 內的欄位名稱 */
	private static final String MAP_KEY_LOG_ID = "logId";
	private static final String MAP_KEY_SECONDS = "seconds";

	/**
	 * 最後的心跳包 Key 命名，只用sysUserId是避免同時瀏覽複數課程<br>
	 * heartbeat:{sysUserId}:last_beat
	 * 
	 * @param sysUserId
	 * @return
	 */
	private String lastBeatKey(Long sysUserId) {
		return "heartbeat:" + sysUserId + ":last_beat";
	}

	/**
	 * 觀看的秒數(期間) Key 命名，只用sysUserId是避免同時瀏覽複數課程<br>
	 * heartbeat:{sysUserId}:duration
	 * 
	 * @param sysUserId
	 * @return
	 */
	private String durationKey(Long sysUserId) {
		return "heartbeat:" + sysUserId + ":duration";
	}

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

		// 如果此章節還未完成，變更章節學習狀態 及 課程狀態 
		if (!CourseStatusEnum.COMPLETED.equals(chapterProgress.getStatus())) {

			// 當今天訪問 非測驗型章節，直接代表他學習完了
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

				// 如果課程處於未開始的狀態，更改程進行中，並設定第一次學習的時間
				if (CourseStatusEnum.NOT_STARTED.equals(courseEnrollment.getStatus())) {
					courseEnrollment.setStatus(CourseStatusEnum.IN_PROGRESS);
					courseEnrollment.setStartedAt(now);
				}

				// 更新學習狀態
				courseEnrollmentService.updateById(courseEnrollment);

			} else {
				// 測驗型章節,則先處理進行中,等問卷回答完成,回調時在更新狀態
				chapterProgress.setStatus(CourseStatusEnum.IN_PROGRESS);
			}

		}
		// 章節學習進度更新
		chapterProgressService.updateById(chapterProgress);

		/** -------------------- Redis 中建立 觀看session ------------------------- */

		// 如果當前已經有心跳包，則直接拋出錯誤，這是為了防止多個頁面觀看，刷時數
		RBucket<Long> lastBeat = redissonClient.getBucket(lastBeatKey(sysUserVO.getSysUserId()));

		// duration map 同時存 logId 和累積秒數，兩個都剛好是Long類型，所以可以這樣操作，更複雜的情況建議分開
		RMap<String, Long> durationMap = redissonClient.getMap(durationKey(sysUserVO.getSysUserId()),
				LongCodec.INSTANCE);

		// 如果已經有心跳包，則要判斷是否是當前課程的心跳包
		if (lastBeat.isExists()) {

			System.out.println("心跳包已存在");

			// 先取出觀看紀錄的快取，拿到當前紀錄資料
			Long watchLogId = durationMap.get(MAP_KEY_LOG_ID);
			ChapterWatchLog chapterWatchLog = chapterWatchLogService.get(watchLogId);

			// 如果判斷是當前課程
			if (chapterWatchLog.getChapterProgressId().equals(chapterProgressId)) {

				// 把此心跳包對應的watchLog更新，把觀看結束時間填入，結束上次Session紀錄
				// 心跳包只會存活90秒 , 先簡易拿當前時間，去相減，真的有問題再拿durationKey 中的時間去減
				chapterWatchLog.setSessionEnd(LocalDateTime.now());
				int seconds = (int) Duration.between(chapterWatchLog.getSessionStart(), chapterWatchLog.getSessionEnd())
						.getSeconds();
				chapterWatchLog.setDurationSec(seconds);
				chapterWatchLogService.updateById(chapterWatchLog);
			} else {
				// 不是當前課程，代表用戶正在嘗試重複觀看課程，直接拋出錯誤
				throw new ChapterWatchLogException("系統偵測到有重複開啟上課介面，禁止多重視窗瀏覽課程");
			}
		}

		// 獲取當前時間
		long now = Instant.now().getEpochSecond();

		// 沒有心跳包，設置當前時間, 
		lastBeat.set(now);
		lastBeat.expire(LAST_BEAT_TTL);

		// 此時DB創建一個 觀看紀錄
		ChapterWatchLog chapterWatchLog = chapterWatchLogService.create(chapterProgressId,
				chapterProgress.getCourseEnrollmentId(), sysUserVO.getSysUserId(),
				chapterProgress.getCourseChapterId());

		durationMap.put(MAP_KEY_LOG_ID, chapterWatchLog.getChapterWatchLogId());
		durationMap.put(MAP_KEY_SECONDS, 0L);
		durationMap.expire(DURATION_TTL);

		// 回傳觀看紀錄,讓前端拿到紀錄ID , 定期call API 維持心跳包
		return chapterWatchLog;

	}

	/**
	 * 根據 報名ID 查詢 課程單元樹(父子結構)
	 * 
	 * @param courseEnrollmentId
	 * @return
	 */
	public List<CourseChapterVO> findTreeList(Long courseEnrollmentId) {

		// 1.拿到報名資料
		CourseEnrollment courseEnrollment = courseEnrollmentService.get(courseEnrollmentId);

		// 2.拿到當初報名時的章節Ids
		List<ChapterProgress> chapterProgresses = chapterProgressService.findByEnrollment(courseEnrollmentId);
		List<Long> courseChapterIds = chapterProgresses.stream().map(ChapterProgress::getCourseChapterId).toList();

		// 3.透過 課程ID 查到所有章節的資訊
		List<CourseChapterVO> courseChapterVO = courseChapterService
				.findTreeSourceByCourseId(courseEnrollment.getCourseId());

		// 4.過濾資料
		List<CourseChapterVO> filtered = courseChapterVO.stream()
				.filter(vo -> ChapterContentTypeEnum.DIRECTORY.equals(vo.getContentType())
						|| courseChapterIds.contains(vo.getCourseChapterId()))
				.toList();

		// 5.拿著過濾後的資料組成樹狀結構,並在進行一次過濾空目錄，
		List<CourseChapterVO> tree = TreeUtil.buildTree(filtered);
		tree.removeIf(this::pruneDirectory);

		return tree;
	};

	/**
	 * 遞歸處理節點內的空目錄
	 * 
	 * @param node
	 * @return
	 */
	private boolean pruneDirectory(CourseChapterVO node) {

		if (node.getChildren() != null) {
			node.getChildren().removeIf(this::pruneDirectory);
		}

		boolean isDirectory = node.getContentType() == ChapterContentTypeEnum.DIRECTORY;
		boolean hasChildren = node.getChildren() != null && !node.getChildren().isEmpty();

		return isDirectory && !hasChildren;
	}

}
