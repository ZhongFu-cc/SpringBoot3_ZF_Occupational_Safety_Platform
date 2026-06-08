package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CompanyStatusEnum;

@Data
public class PutCompanyDTO {

	@NotNull
    @Schema(description = "主鍵ID")
    private Long companyId;

	@NotBlank
    @Schema(description = "公司名")
    private String name;

	@NotNull
    @Schema(description = "啟用狀態")
    private CompanyStatusEnum status;
	
}
