package tw.com.zf_occupational_safety_platform.pojo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginDTO {

	@NotBlank
	@Schema(description = "帳號")
	private String account;

	@NotBlank
	@Schema(description = "密碼")
	private String password;
	
	@NotBlank
	@Schema(description = "驗證碼key")
	private String verificationKey;
	
	@NotBlank
	@Schema(description = "用戶輸入的驗證碼")
	private String verificationCode;
	
	
}
