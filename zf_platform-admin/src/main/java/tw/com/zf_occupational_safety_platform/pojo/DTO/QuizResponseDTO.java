package tw.com.zf_occupational_safety_platform.pojo.DTO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddResponseAnswerDTO;

@Data
public class QuizResponseDTO {

	@Schema(description = "表單ID")
	@NotNull
	private Long formId;

	@Schema(description = "會員ID , 不是必填 , require_login 為 1 時會有值")
	private Long memberId;

	@Schema(description = "章節學習進度ID")
	@NotNull
	private Long chapterProgressId;

	@Schema(description = "回覆值")
	@NotNull
	@Valid
	private List<AddResponseAnswerDTO> responseAnswer;

}
