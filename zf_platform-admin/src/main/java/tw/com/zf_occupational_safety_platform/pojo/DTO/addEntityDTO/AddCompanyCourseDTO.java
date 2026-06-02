package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCompanyCourseDTO {
	@NotNull
	@Schema(description = "公司 ID")
	private Long companyId;

	@NotNull
	@Schema(description = "課程ID")
	private Long courseId;
}
