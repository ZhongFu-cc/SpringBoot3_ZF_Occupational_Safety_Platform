package tw.com.zf_occupational_safety_platform.system.pojo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 前端往後端傳送的登入信息
 */

@Data
public class LoginInfo {

	@NotBlank 
	private String account;
	
	@NotBlank
	private String password;
	
}
