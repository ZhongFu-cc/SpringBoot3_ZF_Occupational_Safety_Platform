package tw.com.zf_occupational_safety_platform.controller;

import java.io.IOException;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.manager.CourseEnrollmentManager;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseEnrollmentVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningRecordSummaryVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningRecordVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 課程報名表：記錄用戶報名課程及整體完成狀態 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Tag(name = "課程報名表 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course-enrollment")
public class CourseEnrollmentController {

	private final AuthManager authManager;
	private final CourseEnrollmentManager courseEnrollmentManager;

	/**
	 * 臨時DTO,只收 courseId 參數<br>
	 */
	public record EnrollCourseRequest(@NotNull @Schema(description = "課程ID", type = "string") Long courseId) {
	}

	/**
	 * 根據ID 查詢 報名課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 報名課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@GetMapping("{id}")
	@SaCheckLogin
	public R<CourseEnrollmentVO> getCourseEnrollment(@PathVariable("id") @Schema(type = "string") Long id) {
		CourseEnrollmentVO vo = courseEnrollmentManager.getByOwner(id);
		return R.ok(vo);
	}

	/**
	 * 查詢 報名課程分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 報名課程分頁對象")
	@SaCheckLogin
	public R<IPage<CourseEnrollmentVO>> findCourseEnrollmentPageByOwner(@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam(required = false) @Schema(description = "可選值:not_started、in_progress、completed、expired、cancelled") String status) {

		// 可傳可不傳 , 不傳的情況下手動調整為null , 避免轉換失敗
		CourseStatusEnum courseStatusEnum;
		if (status == null) {
			courseStatusEnum = null;
		} else {
			courseStatusEnum = CourseStatusEnum.fromValue(status);
		}

		SysUserVO sysUserVO = authManager.getUserInfo();

		Page<CourseEnrollment> pageInfo = new Page<>(page, size);
		IPage<CourseEnrollmentVO> voPage = courseEnrollmentManager.findPageByOwner(pageInfo, courseStatusEnum,
				sysUserVO);

		return R.ok(voPage);
	}

	/**
	 * 查詢 學習歷程 分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("learning-record")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 學習歷程 分頁對象")
	@SaCheckLogin
	public R<IPage<LearningRecordVO>> findLearningRecordPageByOwner(@RequestParam Integer page,
			@RequestParam Integer size, @RequestParam(required = false) String queryText,
			@RequestParam(required = false) @Schema(description = "可選值:not_started、in_progress、completed、expired、cancelled") String status) {

		// 可傳可不傳 , 不傳的情況下手動調整為null , 避免轉換失敗
		CourseStatusEnum courseStatusEnum;
		if (status == null) {
			courseStatusEnum = null;
		} else {
			courseStatusEnum = CourseStatusEnum.fromValue(status);
		}

		SysUserVO sysUserVO = authManager.getUserInfo();

		Page<CourseEnrollment> pageInfo = new Page<>(page, size);
		IPage<LearningRecordVO> voPage = courseEnrollmentManager.findLearningRecordByOwner(pageInfo, courseStatusEnum,
				queryText, sysUserVO);

		return R.ok(voPage);
	}

	/**
	 * 查詢 學習歷程 統計摘要（KPI 卡片 / 狀態分布圖 / 即將到期提醒）
	 *
	 * @return
	 */
	@GetMapping("learning-record/summary")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 學習歷程 統計摘要（KPI 卡片 / 狀態分布圖 / 即將到期提醒）")
	@SaCheckLogin
	public R<LearningRecordSummaryVO> findLearningRecordSummaryByOwner() {
		SysUserVO sysUserVO = authManager.getUserInfo();
		LearningRecordSummaryVO vo = courseEnrollmentManager.getLearningRecordSummary(sysUserVO);
		return R.ok(vo);
	}

	/**
	 * 報名 課程
	 *
	 * @param courseId 課程ID
	 * @return
	 */
	@Operation(summary = "報名 課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@PostMapping
	@SaCheckLogin
	public R<Void> enrollCourse(@RequestBody @Valid EnrollCourseRequest enrollCourseRequest) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		courseEnrollmentManager.enrollCourse(enrollCourseRequest.courseId(), sysUserVO);
		return R.ok();
	}

	/**
	 * 取消報名課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "取消報名課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@DeleteMapping("{id}")
	@SaCheckLogin
	public R<Void> cancelEnrollment(@PathVariable @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		courseEnrollmentManager.cancelEnrollment(id, sysUserVO);
		return R.ok();
	}

	@Operation(summary = "下載 個人 學習歷程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@PostMapping("download-study-history")
	@SaCheckLogin
	public R<Void> downloadStudyHistory(HttpServletResponse response) throws IOException {
		SysUserVO sysUserVO = authManager.getUserInfo();
		courseEnrollmentManager.downloadStudyHistory(response, sysUserVO);
		return R.ok();
	}

}
