package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class AnswerResultVO {

	@Schema(description = "是否回答正確")
	private CommonStatusEnum isCorrect;

	@Schema(description = "正確答案")
	private String correctAnswer;

	@Schema(description = "您的答案")
	private String yourAnswer;

}
