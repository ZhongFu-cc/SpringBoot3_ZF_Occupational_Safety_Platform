package tw.com.zf_occupational_safety_platform.controller;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.manager.CourseEnrollmentManager;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
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
	private final CourseEnrollmentService courseEnrollmentService;
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
	public R<CourseEnrollment> getCourseEnrollment(@PathVariable("id") @Schema(type = "string") Long id) {
		CourseEnrollment courseEnrollment = courseEnrollmentService.get(id);
		return R.ok(courseEnrollment);
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
	public R<IPage<CourseEnrollment>> findCourseEnrollmentPageByOwner(@RequestParam Integer page,
			@RequestParam Integer size, @RequestParam(required = false) CourseStatusEnum status) {

		SysUserVO sysUserVO = authManager.getUserInfo();

		Page<CourseEnrollment> pageInfo = new Page<>(page, size);
		IPage<CourseEnrollment> courseEnrollmentPage = courseEnrollmentService.findPageByOwner(pageInfo, status,
				sysUserVO.getSysUserId());
		return R.ok(courseEnrollmentPage);
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

}
