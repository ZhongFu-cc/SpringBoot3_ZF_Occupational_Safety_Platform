package tw.com.zf_occupational_safety_platform.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;

/**
 * <p>
 * 課程報名表：記錄用戶報名課程及整體完成狀態
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("course_enrollment")
@Schema(name = "CourseEnrollment", description = "課程報名表：記錄用戶報名課程及整體完成狀態")
public class CourseEnrollment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主鍵ID")
	@TableId("course_enrollment_id")
	private Long courseEnrollmentId;
	
    @Schema(description = "公司 ID")
    @TableField("company_id")
    private Long companyId;

	@Schema(description = "用戶部門ID")
	@TableField("department_id")
	private Long departmentId;
    
	@Schema(description = "報名用戶ID")
	@TableField("sys_user_id")
	private Long sysUserId;

	@Schema(description = "報名課程ID")
	@TableField("course_id")
	private Long courseId;

	@Schema(description = "整體報名/學習狀態")
	@TableField("status")
	private CourseStatusEnum status;

	@Schema(description = "課程中應完成的章節數（排除 content_type=directory）")
	@TableField("total_chapters")
	private Integer totalChapters;

	@Schema(description = "用戶已完成的章節數")
	@TableField("completed_chapters")
	private Integer completedChapters;

	@Schema(description = "是否完成所有章節; 0=否, 1=是")
	@TableField("is_chapters_done")
	private CommonStatusEnum isChaptersDone;

	@Schema(description = "已累積的有效觀看秒數（去除快轉、重複）")
	@TableField("accumulated_seconds")
	private Integer accumulatedSeconds;

	@Schema(description = "課程要求的總秒數（課程建立時快照，防止課程修改影響舊報名）")
	@TableField("required_seconds")
	private Integer requiredSeconds;

	@Schema(description = "時數是否達標; 0=否, 1=是")
	@TableField("is_minutes_met")
	private CommonStatusEnum isMinutesMet;

	@Schema(description = "報名時間")
	@TableField("enrolled_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime enrolledAt;

	@Schema(description = "首次開始學習時間")
	@TableField("started_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startedAt;

	@Schema(description = "達成 completed 狀態的時間")
	@TableField("completed_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completedAt;

	@Schema(description = "課程有效期限（NULL=無期限）")
	@TableField("expired_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expiredAt;

	@Schema(description = "創建者")
	@TableField("create_by")
	private String createBy;

	@Schema(description = "創建時間")
	@TableField(value = "create_date", fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	@Schema(description = "最後修改者")
	@TableField("update_by")
	private String updateBy;

	@Schema(description = "最後修改時間")
	@TableField(value = "update_date", fill = FieldFill.UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	@Schema(description = "邏輯刪除; 0=活耀, 1=刪除")
	@TableField("is_deleted")
	@TableLogic
	private Integer isDeleted;
}
