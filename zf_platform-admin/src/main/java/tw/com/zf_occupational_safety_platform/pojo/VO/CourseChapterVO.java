package tw.com.zf_occupational_safety_platform.pojo.VO;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.utils.TreeNode;

@Data
public class CourseChapterVO implements TreeNode<CourseChapterVO, Long> {

	@Schema(description = "主鍵ID")
	@TableId("course_chapter_id")
	private Long courseChapterId;

	@Schema(description = "所屬課程ID")
	private Long courseId;

	@Schema(description = "上級單元ID; 0表示第一層大單元。若有小單元則傳入大單元的chapter_id")
	private Long parentId;

	@Schema(description = "關聯自定義表單表的主鍵ID (當content_type為quiz時必填")
	private Long formId;

	@Schema(description = "單元/章節名稱")
	private String title;

	@Schema(description = "排序欄位, 數字越小越靠前")
	private Integer chapterOrder;

	@Schema(description = "單元內容類型: directory(純目錄/大單元), video(影片內容), quiz(隨堂/課後測驗)")
	private ChapterContentTypeEnum contentType;

	@Schema(description = "若為影片(video)，儲存其播放路徑")
	private String videoUrl;

	/**
	 * 為了整理Catrgory嵌套關係這邊實現自定義的NodeTree接口,並實現一些額外方法 getId()方法, 是要實現NodeTree接口的方法,
	 * 但要使用JsonIgnore來忽略,不然之後輸出結果會多一個id
	 */

	// 創建一個children用來獲取子類別,並且聲明這是一個不存在於數據的字段
	@Schema(description = "子類別")
	private List<CourseChapterVO> children;

	@Override
	@JsonIgnore
	public Long getId() {
		return getCourseChapterId();
	}

	@Override
	public void setChildrenInternal(List<CourseChapterVO> children) {
		this.children = children;

	}

}
