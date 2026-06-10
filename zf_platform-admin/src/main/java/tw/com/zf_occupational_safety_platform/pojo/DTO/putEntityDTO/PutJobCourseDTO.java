package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class PutJobCourseDTO {

	@NotNull
    @Schema(description = "主鍵ID")
    private Long jobCourseId;

	@NotNull
    @Schema(description = "是否為必填項 ; 0=否 , 1 = 是")
    private CommonStatusEnum isMandatory;
	
}
