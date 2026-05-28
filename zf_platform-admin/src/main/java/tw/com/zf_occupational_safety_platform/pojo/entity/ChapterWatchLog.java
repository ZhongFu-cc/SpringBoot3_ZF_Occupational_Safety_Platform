package tw.com.zf_occupational_safety_platform.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 章節觀看明細日誌
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Getter
@Setter
@TableName("chapter_watch_log")
@Schema(name = "ChapterWatchLog", description = "章節觀看明細日誌")
public class ChapterWatchLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主鍵ID")
	@TableId("chapter_watch_log_id")
	private Long chapterWatchLogId;

	@Schema(description = "課程章節學習進度ID")
	@TableField("chapter_progress_id")
	private Long chapterProgressId;

	@Schema(description = "報名ID")
	@TableField("course_enrollment_id")
	private Long courseEnrollmentId;

	@Schema(description = "用戶ID")
	@TableField("sys_user_id")
	private Long sysUserId;

	@Schema(description = "章節ID")
	@TableField("course_chapter_id")
	private Long courseChapterId;

	@Schema(description = "本次觀看時間起點")
	@TableField("session_start")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sessionStart;

	@Schema(description = "本次觀看時間終點")
	@TableField("session_end")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime sessionEnd;

	@Schema(description = "本次觀看秒數 = ended_at_sec - started_at_sec")
	@TableField("duration_sec")
	private Integer durationSec;

	@Schema(description = "創建者")
	@TableField("create_by")
	private String createBy;

	@Schema(description = "創建時間")
	@TableField(value = "create_date", fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	@Schema(description = "邏輯刪除")
	@TableField("is_deleted")
	@TableLogic
	private Integer isDeleted;
}
