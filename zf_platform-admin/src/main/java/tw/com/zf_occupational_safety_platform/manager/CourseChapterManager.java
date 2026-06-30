package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseChapterConvert;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.FormStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddFormDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.Form;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.FormService;

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
	private final FormService formService;
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

		// 如果是 測驗型的,就創建 測驗表單
		if (ChapterContentTypeEnum.QUIZ.equals(addCourseChapterDTO.getContentType())) {

			if (courseChapterService.existQuizChapter(addCourseChapterDTO.getCourseId())) {
				throw new CourseException("此課程已有總測驗章節");
			}

			AddFormDTO addFormDTO = new AddFormDTO();
			addFormDTO.setTitle(addCourseChapterDTO.getTitle());
			addFormDTO.setStatus(FormStatusEnum.PUBLISHED);
			addFormDTO.setAllowMultipleSubmissions(CommonStatusEnum.YES);
			addFormDTO.setRequireLogin(CommonStatusEnum.YES);
			addFormDTO.setRequiredForCheckout(CommonStatusEnum.NO);

			// 創建表單,並把表單 和 章節整合
			Form form = formService.create(addFormDTO);
			addCourseChapterDTO.setFormId(form.getFormId());
			// Quiz總測驗一定為頂層章節
			addCourseChapterDTO.setParentId(0L);

		}

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

		// 先查詢之前的資訊
		CourseChapter currentCourseChapter = courseChapterService.get(putCourseChapterDTO.getCourseChapterId());

		// 章節內容類型 有 變動，接著判斷變動情況
		if (!currentCourseChapter.getContentType().equals(putCourseChapterDTO.getContentType())) {

			// 如果要調整為Quiz，且沒有formId則建立表單
			if (ChapterContentTypeEnum.QUIZ.equals(putCourseChapterDTO.getContentType())
					&& currentCourseChapter.getFormId() == null) {

				if (courseChapterService.existQuizChapter(putCourseChapterDTO.getCourseId())) {
					throw new CourseException("此課程已有總測驗章節");
				}

				AddFormDTO addFormDTO = new AddFormDTO();
				addFormDTO.setTitle(putCourseChapterDTO.getTitle());
				addFormDTO.setStatus(FormStatusEnum.PUBLISHED);
				addFormDTO.setAllowMultipleSubmissions(CommonStatusEnum.YES);
				addFormDTO.setRequireLogin(CommonStatusEnum.YES);
				addFormDTO.setRequiredForCheckout(CommonStatusEnum.NO);

				// 創建表單,並把表單 和 章節整合
				Form form = formService.create(addFormDTO);
				putCourseChapterDTO.setFormId(form.getFormId());
				// Quiz總測驗一定為頂層章節
				putCourseChapterDTO.setParentId(0L);

			}

		}

		// 最後進行更新
		courseChapterService.update(putCourseChapterDTO);

	}

	/**
	 * 刪除課程章節
	 * 
	 * @param courseChapterId
	 */
	public void remove(Long courseChapterId) {

		// 查詢刪除此章節受影響的所有章節
		List<CourseChapter> courseChpaters = courseChapterService.findAllNode(courseChapterId);

		// 最後刪除課程章節
		courseChapterService.remove(courseChapterId);
	}

}
