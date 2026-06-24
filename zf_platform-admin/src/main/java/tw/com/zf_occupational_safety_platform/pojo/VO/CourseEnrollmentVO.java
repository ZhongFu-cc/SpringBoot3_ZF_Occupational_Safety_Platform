package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;

@Data
public class CourseEnrollmentVO {

	@Schema(description = "主鍵ID")
	private Long courseEnrollmentId;

	@Schema(description = "報名用戶ID")
	private Long sysUserId;

	@Schema(description = "報名課程ID")
	private Long courseId;
	
	@Schema(description = "課程類別ID")
	private Long courseCategoryId;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "課程封面圖片路徑")
	private String courseCoverImage;

	@Schema(description = "課程大綱與介紹")
	private String courseDescription;

	@Schema(description = "整體報名/學習狀態")
	private CourseStatusEnum status;

	@Schema(description = "課程中應完成的章節數（排除 content_type=directory）")
	private Integer totalChapters;

	@Schema(description = "用戶已完成的章節數")
	private Integer completedChapters;

	@Schema(description = "是否完成所有章節; 0=否, 1=是")
	private CommonStatusEnum isChaptersDone;

	@Schema(description = "已累積的有效觀看秒數（去除快轉、重複）")
	private Integer accumulatedSeconds;

	@Schema(description = "課程要求的總秒數（課程建立時快照，防止課程修改影響舊報名）")
	private Integer requiredSeconds;

	@Schema(description = "時數是否達標; 0=否, 1=是")
	private CommonStatusEnum isMinutesMet;

	@Schema(description = "報名時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime enrolledAt;

	@Schema(description = "首次開始學習時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startedAt;

	@Schema(description = "達成 completed 狀態的時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@Schema(description = "課程有效期限（NULL=無期限）")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiredAt;

	
}
