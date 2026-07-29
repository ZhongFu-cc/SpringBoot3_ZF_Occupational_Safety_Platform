package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ESG 報告 - 年度教育訓練 KPI 數據<br>
 * 四張 KPI Card 由同一支 API 一次回傳，並附上分子/分母佐證欄位，方便 ESG 報告書對帳稽核
 */
@Data
public class EsgTrainingKpiVO {

	@Schema(description = "統計年度 (例如：2026)")
	private Integer year;

	@Schema(description = "指標一：年度受訓總時數 (例如：1520.5，前端自行加上 hr)")
	private Double totalTrainingHours;

	@Schema(description = "指標二：人均受訓時數 (年度受訓總時數 / 全公司活躍總人數)")
	private Double averageHoursPerEmployee;

	@Schema(description = "指標三：培訓員工總覆蓋率% (該年度有報名紀錄的不重複活躍人數 / 全公司活躍總人數)")
	private Double trainingCoverageRate;

	@Schema(description = "指標四：課程總完課率% (該年度 completed 筆數 / 該年度報名總筆數)")
	private Double overallCompletionRate;

	@Schema(description = "佐證：全公司活躍總人數 (指標二、指標三的分母)")
	private Integer activeEmployeeCount;

	@Schema(description = "佐證：該年度有報名紀錄的不重複活躍人數 (指標三的分子)")
	private Integer trainedEmployeeCount;

	@Schema(description = "佐證：該年度報名總筆數 (指標四的分母)")
	private Integer totalEnrollmentCount;

	@Schema(description = "佐證：該年度完課(completed)總筆數 (指標四的分子)")
	private Integer completedEnrollmentCount;

}
