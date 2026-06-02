package tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDepartmentCourseDTO {

	@NotNull
	@Schema(description = "部門 ID")
	private Long departmentId;
	
	@NotNull
	@Schema(description = "公司課程 ID")
	private Long companyCourseId;
}
