package tw.com.zf_occupational_safety_platform.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 公司 x 作業類別 關聯表
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Getter
@Setter
@TableName("company_job_type")
@Schema(name = "CompanyJobType", description = "公司 x 作業類別 關聯表")
public class CompanyJobType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("company_job_type_id")
    private Long companyJobTypeId;

    @Schema(description = "公司 ID")
    @TableField("company_id")
    private Long companyId;

    @Schema(description = "作業類型 ID")
    @TableField("job_type_id")
    private Long jobTypeId;

    @Schema(description = "創建時間")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private LocalDateTime createDate;
}
