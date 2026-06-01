package tw.com.zf_occupational_safety_platform.manager;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.VO.HeartbeatVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;

/**
 * 章節觀看紀錄 - 管理層
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterWatchLogManager {

	private final CourseEnrollmentService courseEnrollmentService;
	private final ChapterWatchLogService chapterWatchLogService;
	private final RedissonClient redissonClient;

	/** 前端心跳間隔 60 秒，小於 15 秒視為重複呼叫，不累加 */
	private static final long MIN_ELAPSED_SEC = 15L;
	/** elapsed 上限：超過此值只累加 cap，防止開著頁面放著不動 */
	private static final long ELAPSED_CAP_SEC = 60L;

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

	/** ------------------ 心跳包 -------------------------- */
	public HeartbeatVO heartbeat(Long chapterWatchLogId, Long sysUserId) {
		HeartbeatVO vo = new HeartbeatVO();

		RBucket<Long> lastBeatBucket = redissonClient.getBucket(lastBeatKey(sysUserId));
		Long lastBeat = lastBeatBucket.get();

		// 心跳包不存在 -> TTL 已過期，session 無效，請前端重新刷新頁面
		if (!lastBeatBucket.isExists()) {
			vo.setSessionAlive(false);
			vo.setAccumulatedSeconds(0L);
			return vo;
		}

		long now = Instant.now().getEpochSecond();
		long elapsed = now - lastBeat;
		RMap<String, Long> durationMap = redissonClient.getMap(durationKey(sysUserId),LongCodec.INSTANCE);

		// 重複呼叫過快，不累加、不重置 TTL，直接回傳現有數值
		if (elapsed < MIN_ELAPSED_SEC) {
			log.warn("heartbeat 重複呼叫過快 sysUserId={} logId={} elapsed={}s", sysUserId, chapterWatchLogId, elapsed);
		} else {
			// 正常心跳：累加實際經過時間，上限 60 秒，防止開著頁面放著不動
			long actual = Math.min(elapsed, ELAPSED_CAP_SEC);
			durationMap.addAndGet(MAP_KEY_SECONDS, actual);
			// 重置 last_beat 與 TTL
			lastBeatBucket.set(now);
			lastBeatBucket.expire(LAST_BEAT_TTL);
		}

		vo.setSessionAlive(true);
		vo.setAccumulatedSeconds(durationMap.get(MAP_KEY_SECONDS));
		return vo;
	}

	/** --------------------- 供 TTL 過期監聽器呼叫（用戶直接關頁面時） ----------------------- */
	/**
	 * HeartbeatExpiredListener 監聽後回調 <br>
	 * last_beat 過期時 duration 仍存活(TTL 24h)<br>
	 * 讀取後刪除，寫入 DB。
	 */
	public void getDurationAndClean(Long sysUserId) {
		RMap<String, Long> durationMap = redissonClient.getMap(durationKey(sysUserId),LongCodec.INSTANCE);

		// 心跳包消失，如果durationMap還在，代表非自然結束所以做觀看時間補充
		if (durationMap.isExists()) {

			Long logId = durationMap.get(MAP_KEY_LOG_ID);
			Integer seconds = durationMap.get(MAP_KEY_SECONDS).intValue();

			ChapterWatchLog chapterWatchLog = chapterWatchLogService.get(logId);
			LocalDateTime endSession = chapterWatchLog.getSessionStart().plusSeconds(seconds);

			// 更新觀看紀錄
			chapterWatchLog.setSessionEnd(endSession);
			chapterWatchLog.setDurationSec(seconds);
			chapterWatchLogService.updateById(chapterWatchLog);

			durationMap.delete();

			// 更新 DB 的 courseEnrollment，結算當前課程的完成度
			this.updateEnrollmentSecProgress(chapterWatchLog.getCourseEnrollmentId());

		}

	}

	/** --------------------- 正常結束 session ----------------------- */
	/**
	 * 補計最後一段時間後清除 Redis keys，回傳總累積秒數。<br>
	 */
	public void endWatch(Long chapterWatchLogId, Long sysUserId) {

		RBucket<Long> lastBeatBucket = redissonClient.getBucket(lastBeatKey(sysUserId));
		// last_beat 不存在 → TTL 已過期，session 無效
		if (!lastBeatBucket.isExists()) {
			return;
		}

		// 獲取心跳包最後時間，與當前時間做相減
		Long lastBeat = lastBeatBucket.get();
		long now = Instant.now().getEpochSecond();
		long elapsed = now - lastBeat;

		// 正常心跳：累加實際經過時間，超過上限只算上限
		long actual = Math.min(elapsed, ELAPSED_CAP_SEC);

		RMap<String, Long> durationMap = redissonClient.getMap(durationKey(sysUserId),LongCodec.INSTANCE);
		Integer seconds = durationMap.addAndGet(MAP_KEY_SECONDS, actual).intValue();
		Long logId = durationMap.get(MAP_KEY_LOG_ID);

		//查詢並計算該Session結束時間
		ChapterWatchLog chapterWatchLog = chapterWatchLogService.get(logId);
		LocalDateTime endSession = chapterWatchLog.getSessionStart().plusSeconds(seconds);

		// 更新觀看紀錄
		chapterWatchLog.setSessionEnd(endSession);
		chapterWatchLog.setDurationSec(seconds);
		chapterWatchLogService.updateById(chapterWatchLog);

		// 正常結束觀看，刪掉兩個key
		lastBeatBucket.delete();
		durationMap.delete();

		// 更新 DB 的 courseEnrollment，結算當前課程的完成度
		this.updateEnrollmentSecProgress(chapterWatchLog.getCourseEnrollmentId());

	}

	/**
	 * 更新課程 時數總進度
	 * 
	 * @param enrollmentId
	 */
	private void updateEnrollmentSecProgress(Long enrollmentId) {

		CourseEnrollment courseEnrollment = courseEnrollmentService.get(enrollmentId);

		// 統計時間，並判斷有沒有達成課程完成時間
		Integer totalDurationSec = chapterWatchLogService.calculateTotalDurationSec(enrollmentId);

		courseEnrollment.setAccumulatedSeconds(totalDurationSec);
		if (totalDurationSec >= courseEnrollment.getRequiredSeconds())
			courseEnrollment.setIsMinutesMet(CommonStatusEnum.YES);

		// 更新課程總進度
		courseEnrollmentService.updateById(courseEnrollment);

	}

}
