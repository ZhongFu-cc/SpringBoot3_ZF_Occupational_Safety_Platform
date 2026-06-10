package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class JobCourseVO {

	@Schema(description = "關聯主鍵ID")
	private Long jobCourseId;

	@Schema(description = "課程ID")
	private Long courseId;

	@Schema(description = "作業類別 ID")
	private Long jobTypeId;

	@Schema(description = "是否為必填項 ; 0=否 , 1 = 是")
	private CommonStatusEnum isMandatory;

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
