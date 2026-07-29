package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ESG 報告 - 各部門培訓多元性與資源分配 (GRI 404)<br>
 * 對應左側橫條圖的每一列，人數一律以「當前活躍員工」為母體
 */
@Data
public class EsgDepartmentTrainingVO {

	@Schema(description = "部門ID", type = "string")
	private Long departmentId;

	@Schema(description = "部門名稱")
	private String departmentName;

	@Schema(description = "該部門活躍員工數 (人均時數與完課率的分母)")
	private Integer activeEmployeeCount;

	@Schema(description = "該部門年度受訓總時數 (例如：360.5)")
	private Double totalTrainingHours;

	@Schema(description = "該部門人均受訓時數 (橫條長度，例如：18.0)")
	private Double averageHoursPerEmployee;

	@Schema(description = "該部門完訓人數 (當年被分配的課全部 completed 才算)")
	private Integer completedEmployeeCount;

	@Schema(description = "該部門完課率% <按人頭> (完訓人數 / 該部門活躍員工數)")
	private Double completionRate;

}
