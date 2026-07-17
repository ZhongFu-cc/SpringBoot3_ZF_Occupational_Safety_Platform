package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LearningProgressTableVO {

	@Schema(description = "用戶ID")
	private Long sysUserId;

	@Schema(description = "用戶名")
	private String userName;

	@Schema(description = "部門ID")
	private Long depatmentId;

	@Schema(description = "部門名")
	private String departmentName;

	@Schema(description = "HR分發的總課程數")
    private Integer totalCourses;

    @Schema(description = "用戶已完成的課程數")
    private Integer completedCourses;

    @Schema(description = "用戶整體的學習狀態 (COMPLETED:已完課, IN_PROGRESS:學習中, NOT_STARTED:未開始)")
    private String overallStatus;

    

}
