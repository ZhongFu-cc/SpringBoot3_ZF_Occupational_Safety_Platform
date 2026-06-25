package tw.com.zf_occupational_safety_platform.pojo.excel;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EmployeeStudyHistoryExcel {

	@ExcelProperty("報名紀錄ID")
	private Long courseEnrollmentId;
	
	@ExcelProperty("用戶")
	private String userName;

	@ExcelProperty("課程類別")
	private String courseCategoryName;

	@ExcelProperty("課程名稱")
	private String courseName;

	@ExcelProperty("整體報名/學習狀態")
	private String status;

	@ExcelProperty("課程中應完成的章節數")
	private Integer totalChapters;

	@ExcelProperty("用戶已完成的章節數")
	private Integer completedChapters;

	@ExcelProperty("用戶是否完成所有章節")
	private String isChaptersDone;

	@ExcelProperty("課程要求的總秒數")
	private Integer requiredSeconds;

	@ExcelProperty("已累積的有效觀看秒數")
	private Integer accumulatedSeconds;

	@ExcelProperty("觀看時數是否達標")
	private String isMinutesMet;

	@ExcelProperty("報名時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime enrolledAt;

	@ExcelProperty("首次開始學習時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startedAt;

	@ExcelProperty("完成課程的時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@ExcelProperty("課程證書有效期限")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiredAt;
}
