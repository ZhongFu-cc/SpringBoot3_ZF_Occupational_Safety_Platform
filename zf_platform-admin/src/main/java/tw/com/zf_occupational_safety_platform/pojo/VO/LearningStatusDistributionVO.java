package tw.com.zf_occupational_safety_platform.pojo.VO;

import lombok.Data;

@Data
public class LearningStatusDistributionVO {

	/**
	 * 狀態顯示名稱 (例如: 已完成, 進行中, 未開始)
	 * 方便前端直接拿去當作環狀圖的 Label 顯示
	 */
	private String statusName;

	/**
	 * 該狀態的總人數 (或總人次)
	 */
	private Integer count;

	/**
	 * 該狀態佔整體的百分比 (例如 70.0 代表 70.0%)
	 */
	private Double percentage;

}
