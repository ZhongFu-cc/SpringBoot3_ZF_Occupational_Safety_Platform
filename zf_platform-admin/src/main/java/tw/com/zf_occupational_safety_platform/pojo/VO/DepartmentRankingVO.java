package tw.com.zf_occupational_safety_platform.pojo.VO;

import lombok.Data;

@Data
public class DepartmentRankingVO {
	/**
     * 部門 ID
     */
    private Long departmentId;

    /**
     * 部門名稱
     */
    private String departmentName;

    /**
     * 完課率 (百分比，例如 90.0 代表 90.0%)
     */
    private Double completionRate;
}
