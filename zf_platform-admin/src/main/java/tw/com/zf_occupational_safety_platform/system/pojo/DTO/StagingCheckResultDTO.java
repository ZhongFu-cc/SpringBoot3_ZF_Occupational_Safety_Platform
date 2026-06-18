package tw.com.zf_occupational_safety_platform.system.pojo.DTO;

import lombok.Data;

@Data
public class StagingCheckResultDTO {
	private String account;
    private String email;
    private String dupType; // 用來區分是哪種重複：EXCEL_ACCOUNT, EXCEL_EMAIL, DB_DUP
}
