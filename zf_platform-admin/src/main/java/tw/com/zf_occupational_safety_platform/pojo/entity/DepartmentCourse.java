package tw.com.zf_occupational_safety_platform.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 部門 x 公司課程表
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Getter
@Setter
@TableName("department_course")
@Schema(name = "DepartmentCourse", description = "部門 x 公司課程表")
public class DepartmentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("department_course_id")
    private Long departmentCourseId;

    @Schema(description = "部門 ID")
    @TableField("department_id")
    private Long departmentId;

    @Schema(description = "公司課程 ID")
    @TableField("company_course_id")
    private Long companyCourseId;

    @Schema(description = "創建時間")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
}
