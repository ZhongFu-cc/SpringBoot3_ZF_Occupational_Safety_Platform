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
public class LearningRecordVO {

	@Schema(description = "主鍵ID")
	private Long courseEnrollmentId;

	@Schema(description = "報名用戶ID")
	private Long sysUserId;

	@Schema(description = "報名課程ID")
	private Long courseId;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "課程學習狀態")
	private CourseStatusEnum status;

	@Schema(description = "課程完成進度")
	private String progress;

	@Schema(description = "是否完成所有章節; 0=否, 1=是")
	private CommonStatusEnum isChaptersDone;

	@Schema(description = "累積時長")
	private Integer accumulatedSeconds;

	@Schema(description = "時數是否達標; 0=否, 1=是")
	private CommonStatusEnum isMinutesMet;

	@Schema(description = "首次開始學習時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startedAt;

	@Schema(description = "達成 completed 狀態的時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@Schema(description = "課程證明有效期限（NULL=尚未有期限）")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiredAt;

	@Schema(description = "是否完成課程; 0=否, 1=是")
	private CommonStatusEnum isCompleted;

}
