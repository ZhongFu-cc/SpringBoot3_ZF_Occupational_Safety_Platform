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
 * 章節學習進度：每個報名者 × 章節的完成狀態
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("chapter_progress")
@Schema(name = "ChapterProgress", description = "章節學習進度：每個報名者 × 章節的完成狀態")
public class ChapterProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("chapter_progress_id")
    private Long chapterProgressId;

    @Schema(description = "報名ID")
    @TableField("course_enrollment_id")
    private Long courseEnrollmentId;

    @Schema(description = "課程單元ID")
    @TableField("course_chapter_id")
    private Long courseChapterId;

    @Schema(description = "用戶ID")
    @TableField("sys_user_id")
    private Long sysUserId;

    @Schema(description = "課程ID")
    @TableField("course_id")
    private Long courseId;

    @Schema(description = "該章節學習狀態")
    @TableField("status")
    private CourseStatusEnum status;

    @Schema(description = "此章節累積觀看次數")
    @TableField("watch_count")
    private Integer watchCount;

    @Schema(description = "測驗是否通過;0=未通過, 1=通過")
    @TableField("is_quiz_passed")
    private CommonStatusEnum isQuizPassed;

    @Schema(description = "最後一次測驗分數")
    @TableField("quiz_score")
    private Integer quizScore;

    @Schema(description = "測驗作答次數")
    @TableField("quiz_attempts")
    private Integer quizAttempts;

    @Schema(description = "章節達成 completed 的時間")
    @TableField("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

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
