package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class PutJobTypeDTO {

	@NotNull
    @Schema(description = "主鍵ID")
    private Long jobTypeId;

	@NotBlank
    @Schema(description = "作業類別名稱")
    private String name;

    @Schema(description = "作業類別描述")
    private String description;

    @Schema(description = "是否啟用 ; 0=否 , 1=是")
    private CommonStatusEnum isActive;
	
}
