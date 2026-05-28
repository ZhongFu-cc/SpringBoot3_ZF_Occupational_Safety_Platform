package tw.com.zf_occupational_safety_platform.manager;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.pojo.VO.HeartbeatVO;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;

/**
 * 章節觀看紀錄 - 管理層
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterWatchLogManager {

	private final ChapterWatchLogService chapterWatchLogService;
	private final RedissonClient redissonClient;

	/** 前端心跳間隔 30 秒，小於 5 秒視為重複呼叫，不累加 */
	private static final long MIN_ELAPSED_SEC = 5L;
	/** elapsed 上限：超過此值只累加 cap，防止開著頁面放著不動 */
	private static final long ELAPSED_CAP_SEC = 60L;
	/** last_beat TTL = 心跳間隔 3 倍，容許偶爾一次網路抖動 */
	private static final long LAST_BEAT_TTL_SEC = 90L;
	/** duration 保留 24 小時，確保 last_beat 過期事件觸發時還能讀到 */
	private static final long DURATION_TTL_SEC = 86400L;

	// ── Key 命名 ────────────────────────────────────────────────────────────
	// heartbeat:{sysUserId}:{chapterWatchLogId}:last_beat
	// heartbeat:{sysUserId}:{chapterWatchLogId}:duration
	private String lastBeatKey(Long sysUserId, Long logId) {
		return "heartbeat:" + sysUserId + ":" + logId + ":last_beat";
	}

	private String durationKey(Long sysUserId, Long logId) {
		return "heartbeat:" + sysUserId + ":" + logId + ":duration";
	}

	// ── 心跳（由 Service.heartbeat 呼叫）────────────────────────────────────
	public HeartbeatVO heartbeat(Long chapterWatchLogId, Long sysUserId) {
		HeartbeatVO vo = new HeartbeatVO();

		RBucket<Long> lastBeatBucket = redissonClient.getBucket(lastBeatKey(sysUserId, chapterWatchLogId));
		Long lastBeat = lastBeatBucket.get();

		// last_beat 不存在 → TTL 已過期，session 無效
		if (lastBeat == null) {
			vo.setSessionAlive(false);
			vo.setAccumulatedSeconds(0L);
			return vo;
		}

		long now = Instant.now().getEpochSecond();
		long elapsed = now - lastBeat;
		RAtomicLong durationBucket = redissonClient.getAtomicLong(durationKey(sysUserId, chapterWatchLogId));

		if (elapsed < MIN_ELAPSED_SEC) {
			// 重複呼叫過快，不累加、不重置 TTL，直接回傳現有數值
			log.warn("heartbeat 重複呼叫過快 sysUserId={} logId={} elapsed={}s", sysUserId, chapterWatchLogId, elapsed);
		} else {
			// 正常心跳：累加實際經過時間，上限 60 秒，防止開著頁面放著不動
			long actual = Math.min(elapsed, ELAPSED_CAP_SEC);
			durationBucket.addAndGet(actual);
			// 重置 last_beat 與 TTL
			lastBeatBucket.set(now, LAST_BEAT_TTL_SEC, TimeUnit.SECONDS);
		}

		vo.setSessionAlive(true);
		vo.setAccumulatedSeconds(durationBucket.get());
		return vo;
	}

	// ── 供 TTL 過期監聽器呼叫（用戶直接關頁面時）──────────────────────────
	/**
	 * last_beat 過期時 duration 仍存活（TTL 24h），
	 * 讀取後刪除，由 Service 寫入 DB。
	 */
	public Long getDurationAndClean(Long sysUserId, Long chapterWatchLogId) {
		RAtomicLong durationBucket = redissonClient.getAtomicLong(durationKey(sysUserId, chapterWatchLogId));
		long duration = durationBucket.get();
		durationBucket.delete();
		return duration;
	}

	// -------------- 正常結束 session -----------------------
	/**
	 * 補計最後一段時間後清除 Redis keys，回傳總累積秒數。
	 * 若 last_beat 已不存在（TTL 過期），回傳 null，
	 * 代表已由過期事件處理，Service 層不需重複寫 DB。
	 */
	public void endWatch(Long chapterWatchLogId, Long sysUserId) {
		// TODO Auto-generated method stub
		RBucket<Long> lastBeatBucket = redissonClient.getBucket(lastBeatKey(sysUserId, chapterWatchLogId));
		Long lastBeat = lastBeatBucket.get();

		if (lastBeat == null) {
			// 已由 TTL 過期事件處理
			return;
		}

		// 補計最後一次心跳到 end 呼叫之間的時間
		long now = Instant.now().getEpochSecond();
		long elapsed = now - lastBeat;

		RAtomicLong durationBucket = redissonClient.getAtomicLong(durationKey(sysUserId, chapterWatchLogId));
		if (elapsed >= MIN_ELAPSED_SEC) {
			long actual = Math.min(elapsed, ELAPSED_CAP_SEC);
			durationBucket.addAndGet(actual);
		}

		long total = durationBucket.get();

		// 清除兩個 key
		lastBeatBucket.delete();
		durationBucket.delete();

	}

}
