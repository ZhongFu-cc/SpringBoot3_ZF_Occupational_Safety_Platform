package tw.com.zf_occupational_safety_platform.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
public class CompanyLearningStatisticsExcel {

	@ExcelProperty("員工姓名")
	private String userName;

	@ExcelProperty("部門")
	private String departmentName;

	@ExcelProperty("應修課程數")
	private Integer totalCourses;

	@ExcelProperty("已完成課程數")
	private Integer completedCourses;

	@ExcelProperty("完課率(%)")
	private Double completionRate;

	@ExcelProperty("整體學習狀態")
	private String overallStatus;
}
