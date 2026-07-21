package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 個人學習歷程 統計摘要（KPI 卡片 / 狀態分布圖 / 即將到期提醒 共用）
 */
@Data
public class LearningRecordSummaryVO {

	@Schema(description = "報名課程數（不含已取消）")
	private Integer totalEnrolled;

	@Schema(description = "未開始課程數")
	private Integer notStartedCount;

	@Schema(description = "進行中課程數")
	private Integer inProgressCount;

	@Schema(description = "已完成課程數")
	private Integer completedCount;

	@Schema(description = "已過期課程數")
	private Integer expiredCount;

	@Schema(description = "已取消課程數")
	private Integer cancelledCount;

	@Schema(description = "完成率 (已完成 / 報名數，不含已取消)，例如 36.4")
	private Double completionRate;

	@Schema(description = "累積學習時數（不含已取消），例如 27.2")
	private Double accumulatedHours;

	@Schema(description = "30 天內即將到期的課程證書數量")
	private Integer upcomingExpiryCount;

	@Schema(description = "即將到期的課程明細清單（依到期日由近到遠排序）")
	private List<UpcomingExpiryVO> upcomingExpiryList;

}
