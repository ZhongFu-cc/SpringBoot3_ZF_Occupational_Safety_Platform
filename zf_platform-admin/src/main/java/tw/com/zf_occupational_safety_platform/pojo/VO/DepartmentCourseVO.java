package tw.com.zf_occupational_safety_platform.pojo.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DepartmentCourseVO {

	@Schema(description = "主鍵ID")
	private Long departmentCourseId;

	@Schema(description = "部門 ID")
	private Long departmentId;

	@Schema(description = "公司課程 ID")
	private Long companyCourseId;

	@Schema(description = "課程ID")
	private Long courseId;

	@Schema(description = "課程類別ID")
	private Long courseCategoryId;

	@Schema(description = "課程名稱")
	private String courseName;

	@Schema(description = "課程封面圖片路徑")
	private String courseCoverImage;

	@Schema(description = "課程大綱與介紹")
	private String courseDescription;

	@Schema(description = "本課程設計的總時數(分鐘)")
	private Integer courseTotalMinutes;


}
