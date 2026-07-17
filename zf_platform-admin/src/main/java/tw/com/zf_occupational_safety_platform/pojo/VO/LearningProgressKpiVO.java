package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 課程進度 KPI 數據
 */
@Data
public class LearningProgressKpiVO {

	@Schema(description = "合規達標率<按人頭> (完全通過6門課的人數 / 總人數)")
	private Double completionRate;

	@Schema(description = "課程證照已過期人數 (任意一門證照過期皆算)")
	private Integer expiredCount;

	@Schema(description = "總時數 (例如：120.0，前端自行加上 hr)")
	private Double totalTrainingHours;

	@Schema(description = "待催促人數 (任一門課程狀態為 not_started || pending 皆算)")
	private Integer urgentCount;

}
