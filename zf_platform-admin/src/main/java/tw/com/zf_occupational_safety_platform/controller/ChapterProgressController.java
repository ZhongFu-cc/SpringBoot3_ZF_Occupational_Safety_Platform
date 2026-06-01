package tw.com.zf_occupational_safety_platform.controller;

import java.time.Instant;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.ChapterProgressManager;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 章節學習進度：每個報名者 × 章節的完成狀態 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Tag(name = "章節學習進度 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/chapter-progress")
public class ChapterProgressController {

	private final AuthManager authManager;
	private final ChapterProgressService chapterProgressService;
	private final ChapterProgressManager chapterProgressManager;

	/**
	 * 臨時DTO,只收 chapterProgressId 參數<br>
	 */
	public record ChapterProgressRequest(
			@NotNull @Schema(description = "課程章節進度 ID", type = "string") Long chapterProgressId) {
	}

	@GetMapping
	public void test() {
		System.out.println("系統時間" + Instant.now().getEpochSecond());

	}

	/**
	 * 根據 報名ID 與 課程單元ID 查詢章節學習進度
	 * 
	 * @param courseEnrollmentId 報名ID
	 * @param courseChapterId    課程單元ID
	 * @return 章節學習進度
	 */
	@Operation(summary = "根據 報名ID 與 課程單元ID 查詢章節學習進度")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@GetMapping("by-chapter")
	@SaCheckLogin
	public R<ChapterProgress> getChapterProgress(
			@RequestParam("courseEnrollmentId") @Schema(type = "string") Long courseEnrollmentId,
			@RequestParam("courseChapterId") @Schema(type = "string") Long courseChapterId) {

		SysUserVO sysUserVO = authManager.getUserInfo();
		ChapterProgress chapterProgress = chapterProgressManager.getUserChapterProgress(courseEnrollmentId,
				courseChapterId, sysUserVO);

		return R.ok(chapterProgress);
	}

	/**
	 * 學習 課程章節
	 * 
	 * @param chapterProgressRequest
	 * @return
	 */
	@Operation(summary = "學習 課程章節")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@PutMapping("learning")
	@SaCheckLogin
	public R<ChapterWatchLog> learningChapter(@RequestBody @Valid ChapterProgressRequest chapterProgressRequest) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		ChapterWatchLog chapterWatchLog = chapterProgressManager
				.learningChaprer(chapterProgressRequest.chapterProgressId(), sysUserVO);
		return R.ok(chapterWatchLog);
	}

}
