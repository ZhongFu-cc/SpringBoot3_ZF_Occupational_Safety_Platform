package tw.com.zf_occupational_safety_platform.pojo.excel;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * ESG 審計專用學員學習明細 Excel
 */
@Data
public class EsgLearningDetailExcel {

	@ExcelProperty("姓名")
	private String userName;

	@ExcelProperty("部門")
	private String departmentName;

	@ExcelProperty("課程名稱")
	private String courseName;

	@ExcelProperty("課程狀態")
	private String status;

	@ExcelProperty("完課時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@ExcelProperty("實際投入時數")
	private Double accumulatedHours;

	@ExcelProperty("應修時數")
	private Double requiredHours;

	@ExcelProperty("是否完課取得證書")
	private String isCompleted;

	@ExcelProperty("目前是否在職")
	private String isActive;

}
