package tw.com.zf_occupational_safety_platform.system.pojo.entity;

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

/**
 * <p>
 * 用戶表 - 存取系統用戶個人信息
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Getter
@Setter
@TableName("sys_user")
@Schema(name = "SysUser", description = "用戶表 - 存取系統用戶個人信息")
public class SysUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId("sys_user_id")
	@Schema(description = "主鍵ID",type = "string")
	private Long sysUserId;

	@Schema(description = "父級ID",type = "string")
	@TableField("parent_id")
	private Long parentId;

	@Schema(description = "帳號")
	@TableField("account")
	private String account;

	@Schema(description = "密碼")
	@TableField("password")
	private String password;

	@Schema(description = "真實姓名")
	@TableField("real_name")
	private String realName;

	@Schema(description = "信箱")
	@TableField("email")
	private String email;

	@Schema(description = "電話號碼")
	@TableField("phone")
	private String phone;

	@Schema(description = "公司名稱")
	@TableField("company_name")
	private String companyName;

	@Schema(description = "是否啟用;0=否,1=是")
	@TableField("is_active")
	private CommonStatusEnum isActive;

	@Schema(description = "備註,通常寫這個使用者的主要角色")
	@TableField("remark")
	private String remark;

	@Schema(description = "預設為0代表存在, 更改為1代表刪除")
	@TableField("is_deleted")
	@TableLogic
	private Integer isDeleted;

	@Schema(description = "創建者")
	@TableField(value = "create_by", fill = FieldFill.INSERT)
	private String createBy;

	@Schema(description = "創建時間")
	@TableField(value = "create_date", fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	@Schema(description = "更新者")
	@TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
	private String updateBy;

	@Schema(description = "更新時間")
	@TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

}
