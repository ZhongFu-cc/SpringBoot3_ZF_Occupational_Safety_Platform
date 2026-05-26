package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CourseContentTypeEnum;

@Data
public class PutCourseChapterDTO {

	@NotNull
	@Schema(description = "主鍵ID")
	private Long courseChapterId;

	@NotNull
	@Schema(description = "所屬課程ID")
	private Long courseId;

	@NotNull
	@Schema(description = "上級單元ID; 0表示第一層大單元。若有小單元則傳入大單元的chapter_id")
	private Long parentId;

	@Schema(description = "關聯自定義表單表的主鍵ID (當content_type為quiz時必填")
	private Long formId;

	@NotBlank
	@Schema(description = "單元/章節名稱")
	private String title;

	@Schema(description = "排序欄位, 數字越小越靠前")
	private Integer chapterOrder;

	@NotNull
	@Schema(description = "單元內容類型: directory(純目錄/大單元), video(影片內容), quiz(隨堂/課後測驗)")
	private CourseContentTypeEnum contentType;

}
