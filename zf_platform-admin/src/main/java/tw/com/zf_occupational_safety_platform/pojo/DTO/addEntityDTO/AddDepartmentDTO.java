package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class AddDepartmentDTO {

	@NotNull
	@Schema(description = "公司 ID")
	private Long companyId;

	@NotBlank
	@Schema(description = "部門名")
	private String name;

	@NotNull
	@Schema(description = "是否啟用 ; 0=否 , 1=是")
	private CommonStatusEnum isActive;

}
