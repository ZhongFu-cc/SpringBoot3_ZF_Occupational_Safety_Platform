package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseChapterConvert;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseService;

/**
 * 課程單元 - 管理層
 */
@Component
@RequiredArgsConstructor
public class CourseChapterManager {

	// 「預設」存储桶名称
	@Value("${spring.cloud.aws.s3.bucketName}") // 注意：这里的 Value key 可能需要对应您的配置
	private String bucketName;

	private final static String COURSE_CHAPTER_BASE_PATH = "course/course_chapter";

	private final CourseService courseService;
	private final CourseChapterService courseChapterService;
	private final CourseChapterConvert courseChapterConvert;
	private final S3Helper s3Helper;

	/**
	 * 查詢課程(單元) 樹狀結構列表
	 * 
	 * @param courseId 課程ID
	 */
	public List<CourseChapterVO> findTreeList(Long courseId) {
		return courseChapterService.findTreeByCourseId(courseId);
	}

	/**
	 * 創建課程單元
	 * 
	 * @param addCourseDTO
	 * @param imgFile
	 */
	public CourseChapter createCourseChapter(AddCourseChapterDTO addCourseChapterDTO) {
		CourseChapter courseChapter = courseChapterService.create(addCourseChapterDTO);
		return courseChapter;
	}

	/**
	 * 更新課程單元
	 * 
	 * @param putCourseDTO
	 * @param imgFile
	 */
	public void updateCourseChapter(PutCourseChapterDTO putCourseChapterDTO) {
		courseChapterService.update(putCourseChapterDTO);

	}

}
