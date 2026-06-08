package tw.com.zf_occupational_safety_platform.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Getter
@Setter
@TableName("job_type_course_category")
@Schema(name = "JobTypeCourseCategory", description = "作業類別 x 課程類別 關聯表")
public class JobTypeCourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("type_category_id")
    private Long typeCategoryId;

    @Schema(description = "課程類別 ID")
    @TableField("course_category_id")
    private Long courseCategoryId;

    @Schema(description = "作業類別 ID")
    @TableField("job_type_id")
    private Long jobTypeId;

    @Schema(description = "是否為必填項 ; 0=否 , 1 = 是")
    @TableField("is_mandatory")
    private CommonStatusEnum isMandatory;

    @Schema(description = "創建時間")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
}
