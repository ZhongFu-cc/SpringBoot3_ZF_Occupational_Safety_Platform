package tw.com.zf_occupational_safety_platform.pojo.excel;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CompanyLearningStatisticsDetailExcel {

	@ExcelProperty("員工姓名")
	private String userName;

	@ExcelProperty("部門")
	private String departmentName;

	@ExcelProperty("課程名稱")
	private String courseName;

	@ExcelProperty("課程狀態")
	private String status;

	@ExcelProperty("已完成章節數")
	private Integer completedChapters;

	@ExcelProperty("應完成章節數")
	private Integer totalChapters;

	@ExcelProperty("已累積時數")
	private Double accumulatedHours;

	@ExcelProperty("應修時數")
	private Double requiredHours;

	@ExcelProperty("完成時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;
}
