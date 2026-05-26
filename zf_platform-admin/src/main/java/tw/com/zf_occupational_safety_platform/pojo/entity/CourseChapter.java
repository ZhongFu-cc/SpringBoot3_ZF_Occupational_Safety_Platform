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
import tw.com.zf_occupational_safety_platform.enums.CourseContentTypeEnum;

/**
 * <p>
 * 課程章節與自定義表單綁定結構表
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Getter
@Setter
@TableName("course_chapter")
@Schema(name = "CourseChapterManager", description = "課程章節與自定義表單綁定結構表")
public class CourseChapter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主鍵ID")
	@TableId("course_chapter_id")
	private Long courseChapterId;

	@Schema(description = "所屬課程ID")
	@TableField("course_id")
	private Long courseId;

	@Schema(description = "上級單元ID; 0表示第一層大單元。若有小單元則傳入大單元的chapter_id")
	@TableField("parent_id")
	private Long parentId;

	@Schema(description = "關聯自定義表單表的主鍵ID (當content_type為quiz時必填")
	@TableField("form_id")
	private Long formId;

	@Schema(description = "單元/章節名稱")
	@TableField("title")
	private String title;

	@Schema(description = "排序欄位, 數字越小越靠前")
	@TableField("chapter_order")
	private Integer chapterOrder;

	@Schema(description = "單元內容類型: directory(純目錄/大單元), video(影片內容), quiz(隨堂/課後測驗)")
	@TableField("content_type")
	private CourseContentTypeEnum contentType;

	@Schema(description = "若為影片(video)，儲存其播放路徑")
	@TableField("video_url")
	private String videoUrl;

	@Schema(description = "影片長度(秒), 用於精確學習時數計算")
	@TableField("duration_seconds")
	private Integer durationSeconds;

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

	@Schema(description = "邏輯刪除,預設為0活耀,1為刪除")
	@TableField("is_deleted")
	@TableLogic
	private Integer isDeleted;
}
