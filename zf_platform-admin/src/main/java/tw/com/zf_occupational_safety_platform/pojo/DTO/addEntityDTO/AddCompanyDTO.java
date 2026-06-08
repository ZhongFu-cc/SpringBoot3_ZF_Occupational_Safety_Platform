package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import java.util.Collection;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CompanyStatusEnum;

@Data
public class AddCompanyDTO {
	
	@NotBlank
    @Schema(description = "公司名")
    private String name;

    @Schema(description = "啟用狀態")
    private CompanyStatusEnum status;
    
    @NotNull
    @Min(1)
    @Schema(description = "選擇的作業類別Ids")
    private Collection<Long> jobTypeIds;
    
	
}
