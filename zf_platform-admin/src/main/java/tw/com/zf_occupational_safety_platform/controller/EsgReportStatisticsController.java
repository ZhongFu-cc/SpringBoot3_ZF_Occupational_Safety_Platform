package tw.com.zf_occupational_safety_platform.controller;

import java.io.IOException;

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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.EsgReportStatisticsManager;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgLearningDetailVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgTrainingChartVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgTrainingKpiVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * ESG 報告統計 - 供 HR 製作企業 ESG 報告書使用
 * </p>
 *
 * @author Joey
 * @since 2026-07-28
 */
@Tag(name = "ESG 報告統計 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/esg-report-statistics")
public class EsgReportStatisticsController {

	private final AuthManager authManager;
	private final EsgReportStatisticsManager esgReportStatisticsManager;

	/**
	 * 查詢年度教育訓練 ESG KPI<br>
	 * 一次回傳四張 KPI Card：年度受訓總時數、人均受訓時數、培訓員工總覆蓋率、課程總完課率
	 *
	 * @param year 統計年度，非必要，不帶則為當年
	 * @return
	 */
	@Operation(summary = "查詢年度教育訓練 ESG KPI")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("annual-training-kpi")
	public R<EsgTrainingKpiVO> getAnnualTrainingKpi(
			@RequestParam(required = false) @Schema(description = "統計年度，不帶則為當年") Integer year) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		EsgTrainingKpiVO kpiVO = esgReportStatisticsManager.getAnnualTrainingKpi(sysUserVO, year);
		return R.ok(kpiVO);
	}

	/**
	 * 查詢年度教育訓練 ESG 圖表<br>
	 * 左圖：各部門培訓多元性與資源分配 (人均時數 + 完課率)；右圖：培訓實質成效分佈 (按人頭，四類互斥)
	 *
	 * @param year 統計年度，非必要，不帶則為當年
	 * @return
	 */
	@Operation(summary = "查詢年度教育訓練 ESG 圖表")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("annual-training-chart")
	public R<EsgTrainingChartVO> getAnnualTrainingChart(
			@RequestParam(required = false) @Schema(description = "統計年度，不帶則為當年") Integer year) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		EsgTrainingChartVO chartVO = esgReportStatisticsManager.getAnnualTrainingChart(sysUserVO, year);
		return R.ok(chartVO);
	}

	/**
	 * 分頁查詢 年度學員學習明細 (ESG 審計專用)<br>
	 * 一列 = 一筆修課紀錄 (學員 × 課程)
	 *
	 * @param page        頁碼
	 * @param size        每頁筆數
	 * @param year        統計年度，非必要，不帶則為當年
	 * @param isCompleted 是否完課取得證書；不帶=全部修課紀錄, true=已完課取得證書, false=未完成
	 * @param queryText   查詢條件，比對姓名與課程名稱
	 * @return
	 */
	@Operation(summary = "分頁查詢 年度學員學習明細 (ESG 審計專用)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("annual-learning-detail")
	public R<IPage<EsgLearningDetailVO>> findAnnualLearningDetailPage(@RequestParam Integer page,
			@RequestParam Integer size,
			@RequestParam(required = false) @Schema(description = "統計年度，不帶則為當年") Integer year,
			@RequestParam(required = false) @Schema(description = "不帶=全部修課紀錄, true=已完課取得證書, false=未完成") Boolean isCompleted,
			@RequestParam(required = false) @Schema(description = "查詢條件,比對姓名與課程名稱") String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<CourseEnrollment> pageInfo = new Page<>(page, size);
		IPage<EsgLearningDetailVO> voPage = esgReportStatisticsManager.findAnnualLearningDetailPage(pageInfo, sysUserVO,
				year, isCompleted, queryText);
		return R.ok(voPage);
	}

	/**
	 * 下載 年度學員學習明細 Excel (ESG 審計專用)
	 *
	 * @param response    HTTP響應
	 * @param year        統計年度，非必要，不帶則為當年
	 * @param isCompleted 是否完課取得證書；不帶=全部修課紀錄, true=已完課取得證書, false=未完成
	 * @param queryText   查詢條件，比對姓名與課程名稱
	 */
	@Operation(summary = "下載 年度學員學習明細 Excel (ESG 審計專用)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("annual-learning-detail/download-excel")
	public void downloadAnnualLearningDetailExcel(HttpServletResponse response,
			@RequestParam(required = false) @Schema(description = "統計年度，不帶則為當年") Integer year,
			@RequestParam(required = false) @Schema(description = "不帶=全部修課紀錄, true=已完課取得證書, false=未完成") Boolean isCompleted,
			@RequestParam(required = false) @Schema(description = "查詢條件,比對姓名與課程名稱") String queryText)
			throws IOException {
		SysUserVO sysUserVO = authManager.getUserInfo();
		esgReportStatisticsManager.downloadAnnualLearningDetailExcel(response, sysUserVO, year, isCompleted, queryText);
	}

}
