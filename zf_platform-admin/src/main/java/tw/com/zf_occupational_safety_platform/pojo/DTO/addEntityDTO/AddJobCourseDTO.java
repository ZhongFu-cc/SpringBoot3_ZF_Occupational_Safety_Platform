package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class AddJobCourseDTO {

	@NotNull
	@Schema(description = "課程 ID")
	private Long courseId;

	@NotNull
	@Schema(description = "作業類別 ID")
	private Long jobTypeId;

	@NotNull
	@Schema(description = "是否為必填項 ; 0=否 , 1 = 是")
	private CommonStatusEnum isMandatory;

}
