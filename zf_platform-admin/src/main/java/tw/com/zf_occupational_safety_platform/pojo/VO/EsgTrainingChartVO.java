package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ESG 報告 - 年度教育訓練圖表數據<br>
 * 左圖：各部門培訓多元性與資源分配；右圖：培訓實質成效分佈
 */
@Data
public class EsgTrainingChartVO {

	@Schema(description = "統計年度 (例如：2026)")
	private Integer year;

	@Schema(description = "左圖：各部門培訓多元性與資源分配 (依人均時數由高至低排序，含當年無人受訓的部門)")
	private List<EsgDepartmentTrainingVO> departmentTrainings;

	@Schema(description = "右圖：培訓實質成效分佈 <按人頭，四類互斥> (有效完訓 / 已失效 / 學習中未達標 / 未開始)")
	private List<LearningStatusDistributionVO> effectivenessDistribution;

}
