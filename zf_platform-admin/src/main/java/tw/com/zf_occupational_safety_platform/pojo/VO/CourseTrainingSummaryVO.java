package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "企業管理者-員工課程學習進度 VO")
public class CourseTrainingSummaryVO {

	@Schema(description = "課程ID")
	private Long courseId;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "報名課程人數")
	private Integer totalAssignedCount;

	@Schema(description = "已完成課程人數")
	private Integer completedCount;

	@Schema(description = "學習中人數")
	private Integer inProgressCount;

	@Schema(description = "未開始人數")
	private Integer notStartedCount;

	@Schema(description = "證照已過期人數 (expiredAt < NOW)")
	private Integer expiredCount;

	@Schema(description = "整體完課率 (例如: \"85.5%\")")
	private String completionRate;


}