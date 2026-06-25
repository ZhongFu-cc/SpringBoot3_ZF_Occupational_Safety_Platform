package tw.com.zf_occupational_safety_platform.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.manager.ChapterWatchLogManager;

/**
 * 監聽 Redis Key 過期事件
 * 當 heartbeat:{sysUserId}:last_beat 過期時，自動補寫 DB
 */
@Slf4j
@Component
public class HeartbeatExpiredListener extends KeyExpirationEventMessageListener {

	private static final String LAST_BEAT_PREFIX = "heartbeat:";
	private static final String LAST_BEAT_SUFFIX = ":last_beat";

	private final ChapterWatchLogManager chapterWatchLogManager;

	public HeartbeatExpiredListener(RedisMessageListenerContainer listenerContainer,
			ChapterWatchLogManager chapterWatchLogManager) {

		super(listenerContainer);
		this.chapterWatchLogManager = chapterWatchLogManager;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {

		String expiredKey = message.toString();

		System.out.println("redis key 過期: " + expiredKey);

		log.info("expired key={}", expiredKey);

		if (!expiredKey.startsWith(LAST_BEAT_PREFIX) || !expiredKey.endsWith(LAST_BEAT_SUFFIX)) {
			return;
		}

		try {

			String middle = expiredKey.substring(LAST_BEAT_PREFIX.length(),
					expiredKey.length() - LAST_BEAT_SUFFIX.length());

			Long sysUserId = Long.parseLong(middle);

			chapterWatchLogManager.getDurationAndClean(sysUserId);

		} catch (Exception e) {

			log.error("expired event error", e);
		}
	}
}