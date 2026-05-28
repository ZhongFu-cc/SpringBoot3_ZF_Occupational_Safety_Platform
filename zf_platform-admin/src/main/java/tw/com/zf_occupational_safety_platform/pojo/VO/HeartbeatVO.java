package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class HeartbeatVO {
	@Schema(description = "當前 session 已累積的有效秒數")
	private Long accumulatedSeconds;

	@Schema(description = "當前 s session 是否仍有效（false 代表 TTL 已過期，前端應重新呼叫)")
	private Boolean sessionAlive;

}
