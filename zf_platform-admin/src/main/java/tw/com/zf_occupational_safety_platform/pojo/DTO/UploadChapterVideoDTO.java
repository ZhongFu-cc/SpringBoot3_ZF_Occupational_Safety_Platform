package tw.com.zf_occupational_safety_platform.pojo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.ChunkUploadDTO;

@Data
public class UploadChapterVideoDTO {

	@NotNull
	@Schema(description = "課程章節ID")
	private Long courseChapterId;

	@Valid
	@NotNull
	@Schema(description = "分片上傳資訊")
	private ChunkUploadDTO chunkUploadDTO;

}
