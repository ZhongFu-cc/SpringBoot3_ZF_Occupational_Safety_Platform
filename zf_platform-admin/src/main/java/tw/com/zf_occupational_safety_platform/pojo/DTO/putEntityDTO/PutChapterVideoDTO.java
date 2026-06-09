package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PutChapterVideoDTO {

	@Schema(description = "主鍵ID")
	@NotNull
	private Long chapterVideoId;

	@Schema(description = "課程章節ID")
	@NotNull
	private Long courseChapterId;

	@Schema(description = "檔案名稱-可與傳送時不同")
	@NotBlank
	private String fileName;

}
