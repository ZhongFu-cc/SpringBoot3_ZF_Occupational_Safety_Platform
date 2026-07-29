package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;

/**
 * ESG 報告 - 審計專用學員學習明細<br>
 * 一列 = 一筆修課紀錄 (學員 × 課程)，作為 ESG 報告書的原始佐證資料
 */
@Data
public class EsgLearningDetailVO {

	@Schema(description = "課程報名ID", type = "string")
	private Long courseEnrollmentId;

	@Schema(description = "員工ID", type = "string")
	private Long sysUserId;

	@Schema(description = "姓名")
	private String userName;

	@Schema(description = "部門名稱")
	private String departmentName;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "課程狀態")
	private CourseStatusEnum status;

	@Schema(description = "完課時間 (未完課為 null)")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@Schema(description = "實際投入時數 (例如：3.5)")
	private Double accumulatedHours;

	@Schema(description = "課程應修時數 (例如：3.0)")
	private Double requiredHours;

	@Schema(description = "是否完課取得證書; 0=否, 1=是")
	private CommonStatusEnum isCompleted;

	@Schema(description = "該員工目前是否仍在職(啟用); 0=否, 1=是")
	private CommonStatusEnum isActive;

}
