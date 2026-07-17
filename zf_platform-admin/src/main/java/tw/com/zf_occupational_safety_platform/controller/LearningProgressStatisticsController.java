package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.LearningProgressStatisticsManager;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressChartVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressKpiVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressTableVO;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 課程資訊統計
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Tag(name = "企業課程進度 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course-progress-statistics")
public class LearningProgressStatisticsController {

	private final AuthManager authManager;
	private final LearningProgressStatisticsManager learningProgressStatisticsManager;

	/**
	 * 查詢課程KPI進度
	 * 
	 * @param departmentId 部門ID
	 * @param courseId     課程ID
	 * @return
	 */
	@Operation(summary = "查詢課程KPI進度")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("progress-kpi")
	public R<LearningProgressKpiVO> getLearningProgressKpi(
			@RequestParam(required = false) @Schema(description = "部門ID") Long departmentId,
			@RequestParam(required = false) @Schema(description = "課程ID") Long courseId) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		LearningProgressKpiVO kpiVO = learningProgressStatisticsManager.getCourseProgressKpi(sysUserVO, departmentId,
				courseId);
		return R.ok(kpiVO);
	}

	@Operation(summary = "查詢課程上課進度分佈")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("progress-chart")
	public R<LearningProgressChartVO> getLearningProgressChart(
			@RequestParam(required = false) @Schema(description = "部門ID") Long departmentId,
			@RequestParam(required = false) @Schema(description = "課程ID") Long courseId) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		LearningProgressChartVO vo = learningProgressStatisticsManager.getCourseProgressChart(sysUserVO, departmentId,
				courseId);
		return R.ok(vo);
	}

	@Operation(summary = "查詢課程上課進度詳細資料")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("progress-table")
	public R<IPage<LearningProgressTableVO>> getLearningProgressTable(@RequestParam Integer page,
			@RequestParam Integer size, @RequestParam(required = false) @Schema(description = "部門ID") Long departmentId,
			@RequestParam(required = false) @Schema(description = "課程ID") Long courseId,
			@RequestParam(required = false) @Schema(description = "用戶查詢條件") String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<SysUser> pageInfo = new Page<>(page, size);
		IPage<LearningProgressTableVO> tableVO = learningProgressStatisticsManager.getCourseProgressTable(pageInfo,
				sysUserVO, departmentId, courseId, queryText);
		return R.ok(tableVO);
	}

}
