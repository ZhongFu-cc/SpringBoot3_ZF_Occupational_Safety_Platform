package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 即將到期的課程證書 明細
 */
@Data
public class UpcomingExpiryVO {

	@Schema(description = "課程報名ID")
	private Long courseEnrollmentId;

	@Schema(description = "課程ID")
	private Long courseId;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "課程證明到期時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiredAt;

	@Schema(description = "剩餘天數")
	private Long daysLeft;

}
