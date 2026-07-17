package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.util.List;

import lombok.Data;

@Data
public class LearningProgressChartVO {

	/**
	 * 部門完課排行資料列表 (左圖：條形圖)
	 */
	private List<DepartmentRankingVO> departmentRankings;

	/**
	 * 學習狀態比例資料列表 (右圖：環狀圖)
	 */
	private List<LearningStatusDistributionVO> learningStatusDistribution;

}
