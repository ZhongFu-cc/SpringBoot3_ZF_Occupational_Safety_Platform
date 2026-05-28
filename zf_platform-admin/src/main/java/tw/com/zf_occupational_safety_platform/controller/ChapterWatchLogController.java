package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.ChapterWatchLogManager;
import tw.com.zf_occupational_safety_platform.pojo.VO.HeartbeatVO;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 章節觀看紀錄 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Tag(name = "章節觀看紀錄 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/chapter-watch-log")
public class ChapterWatchLogController {

	private final AuthManager authManager;
	private final ChapterWatchLogManager chapterWatchLogManager;

	/**
	 * 臨時DTO,只收 chapterWatchLogId 參數<br>
	 */
	public record HeartbeatReq(@NotNull @Schema(description = "章節觀看紀錄 ID", type = "string") Long chapterWatchLogId) {
	}

	/**
	 * 心跳 API，前端每 60 秒呼叫一次<br>
	 * 回傳當前累積秒數，前端可用於顯示學習時數
	 */
	@Operation(summary = "心跳上報")
	@PostMapping("/heartbeat")
	public R<HeartbeatVO> heartbeat(@RequestBody @Valid HeartbeatReq req) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		HeartbeatVO heartbeatVO = chapterWatchLogManager.heartbeat(req.chapterWatchLogId(), sysUserVO.getSysUserId());
		return R.ok(heartbeatVO);
	}

	/**
	 * 正常結束觀看（切換章節、主動離開）<br>
	 * 前端在 visibilitychange / 切換章節時呼叫 <br>
	 * 注意：關掉頁面不可靠，還是要有 Redis TTL 過期自動處理
	 */
	@Operation(summary = "結束觀看章節")
	@PostMapping("/end")
	public R<Void> endWatch(@RequestBody @Valid HeartbeatReq req) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		chapterWatchLogManager.endWatch(req.chapterWatchLogId(), sysUserVO.getSysUserId());
		return R.ok();
	}

}
