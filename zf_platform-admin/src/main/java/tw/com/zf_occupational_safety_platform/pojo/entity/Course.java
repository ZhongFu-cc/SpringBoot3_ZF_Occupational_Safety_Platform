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
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

/**
 * <p>
 * 課程主表
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Getter
@Setter
@TableName("course")
@Schema(name = "Course", description = "課程主表")
public class Course implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "主鍵ID")
	@TableId("course_id")
	private Long courseId;

	@Schema(description = "課程類別ID")
	@TableField("course_category_id")
	private Long courseCategoryId;

	@Schema(description = "課程名稱")
	@TableField("title")
	private String title;

	@Schema(description = "課程封面圖片路徑")
	@TableField("cover_image")
	private String coverImage;

	@Schema(description = "課程大綱與介紹")
	@TableField("description")
	private String description;

	@Schema(description = "本課程設計的總時數(分鐘)")
	@TableField("total_minutes")
	private Integer totalMinutes;

	@Schema(description = "是否啟用;0=否,1=是")
	@TableField("is_active")
	private CommonStatusEnum isActive;

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
