package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class CompanyCourseVO {

	@Schema(description = "主鍵ID")
	private Long companyCourseId;

	@Schema(description = "公司 ID")
	private Long companyId;

	@Schema(description = "課程ID")
	private Long courseId;

	@Schema(description = "課程類別ID")
	private Long courseCategoryId;

	@Schema(description = "課程名稱")
	private String title;

	@Schema(description = "課程封面圖片路徑")
	private String coverImage;

	@Schema(description = "課程大綱與介紹")
	private String description;

	@Schema(description = "本課程設計的總時數(分鐘)")
	private Integer totalMinutes;

	@Schema(description = "是否啟用;0=否,1=是")
	private CommonStatusEnum isActive;

}
