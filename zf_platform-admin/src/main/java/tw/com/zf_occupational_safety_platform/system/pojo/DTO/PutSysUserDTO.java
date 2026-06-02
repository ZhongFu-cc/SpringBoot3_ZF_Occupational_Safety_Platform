package tw.com.zf_occupational_safety_platform.system.pojo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PutSysUserDTO {

	@NotNull
	@Schema(description = "主鍵ID")
	private Long sysUserId;

	@Schema(description = "公司ID", type = "string")
	private Long companyId;

	@Schema(description = "部門ID", type = "string")
	private Long departmentId;

	@NotBlank
	@Schema(description = "帳號")
	private String account;

	@NotBlank
	@Schema(description = "密碼")
	private String password;

	@NotBlank
	@Schema(description = "真實姓名")
	private String realName;

	@NotBlank
	@Schema(description = "信箱")
	private String email;

	@NotBlank
	@Schema(description = "電話號碼")
	private String phone;

	@NotBlank
	@Schema(description = "公司名稱")
	private String companyName;

	@Schema(description = "備註,通常寫這個使用者的主要角色")
	private String remark;

}
