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
 * 課程類別 與 法規限制表
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Getter
@Setter
@TableName("course_category")
@Schema(name = "CourseCategory", description = "課程類別 與 法規限制表")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("course_category_id")
    private Long courseCategoryId;

    @Schema(description = "類別名稱 (例如: 基礎職安、高分貝作業環境、高溫環境)")
    @TableField("name")
    private String name;

    @Schema(description = "類別代碼 (用於系統識別或法規綁定代號)")
    @TableField("code")
    private String code;

    @Schema(description = "類別描述")
    @TableField("description")
    private String description;

    @Schema(description = "台灣職安法規最低法定時數限制 (單位-分鐘)")
    @TableField("min_required_minutes")
    private Integer minRequiredMinutes;

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
