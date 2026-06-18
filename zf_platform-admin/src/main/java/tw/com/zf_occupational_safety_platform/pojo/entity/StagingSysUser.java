package tw.com.zf_occupational_safety_platform.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 臨時表-用戶批量匯入
 * </p>
 *
 * @author Joey
 * @since 2026-06-18
 */
@Getter
@Setter
@TableName("staging_sys_user")
@Schema(name = "StagingSysUser", description = "臨時表-用戶批量匯入")
public class StagingSysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主鍵ID")
    @TableId("staging_sys_user_id")
    private Long stagingSysUserId;

    @Schema(description = "Excel匯入ID")
    @TableField("batch_id")
    private String batchId;

    @Schema(description = "父級sys_user_id;企業管理者和企業員工才有")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "公司ID")
    @TableField("company_id")
    private Long companyId;

    @Schema(description = "部門ID")
    @TableField("department_id")
    private Long departmentId;

    @Schema(description = "用戶帳號")
    @TableField("account")
    private String account;

    @Schema(description = "密碼")
    @TableField("password")
    private String password;

    @Schema(description = "真實姓名")
    @TableField("real_name")
    private String realName;

    @Schema(description = "用戶E-mail")
    @TableField("email")
    private String email;

    @Schema(description = "公司名")
    @TableField("company_name")
    private String companyName;

    @Schema(description = "聯絡電話")
    @TableField("phone")
    private String phone;
}
