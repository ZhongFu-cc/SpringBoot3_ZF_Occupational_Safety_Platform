package tw.com.zf_occupational_safety_platform.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.FormStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddFormDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Form;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormResponse;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.FormFieldService;
import tw.com.zf_occupational_safety_platform.service.FormResponseService;
import tw.com.zf_occupational_safety_platform.service.FormService;
import tw.com.zf_occupational_safety_platform.service.ResponseAnswerService;
import tw.com.zf_occupational_safety_platform.system.service.SysChunkFileService;

/**
 * 課程單元 - 管理層
 */
@Component
@RequiredArgsConstructor
public class CourseChapterManager {

	// 「預設」存储桶名称
	@Value("${spring.cloud.aws.s3.bucketName}") // 注意：这里的 Value key 可能需要对应您的配置
	private String bucketName;

	private final CourseEnrollmentService courseEnrollmentService;
	private final CourseChapterService courseChapterService;
	private final ChapterProgressService chapterProgressService;
	private final ChapterWatchLogService chapterWatchLogService;
	private final ChapterVideoService chapterVideoService;
	private final FormService formService;
	private final FormFieldService formFieldService;
	private final FormResponseService formResponseService;
	private final ResponseAnswerService responseAnswerService;
	private final SysChunkFileService sysChunkFileService;
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
	@Transactional
	public void removeCourseChapter(Long courseChapterId) {

		// 1.查詢刪除此章節受影響的所有章節
		List<CourseChapter> courseChapters = courseChapterService.findAllNode(courseChapterId);
		
		// 2.拿到會影響學習歷程的章節，並抽取chapterIds 
		List<CourseChapter> learningChapters = courseChapters.stream()
				.filter(chpater -> !ChapterContentTypeEnum.DIRECTORY.equals(chpater.getContentType()))
				.toList();
		List<Long> learningChapterIds = learningChapters.stream().map(CourseChapter::getCourseChapterId).toList();

		// 3.查找會影響學習歷程資訊的章節進度
		List<ChapterProgress> chapters = chapterProgressService.findByChapter(learningChapterIds);

		// 4.拿到 所有章節 受影響對象(為了修改TotalChapters )
		Map<Long, List<ChapterProgress>> chapterMapByEnrollmentId = chapters.stream()
				.collect(Collectors.groupingBy(ChapterProgress::getCourseEnrollmentId));

		// 5.拿到 已完成章節 受影響對象(為了修改CompletedChapters )
		Map<Long, List<ChapterProgress>> completedChapterMapByEnrollmentId = chapters.stream()
				.filter(chapter -> CourseStatusEnum.COMPLETED.equals(chapter.getStatus()))
				.collect(Collectors.groupingBy(ChapterProgress::getCourseEnrollmentId));

		// 創建待批次處理的 課程狀態
		List<CourseEnrollment> courseEnrollments = new ArrayList<>(completedChapterMapByEnrollmentId.size());

		// 6.遍歷去處理會動到的 課程狀態
		for (Entry<Long, List<ChapterProgress>> chapter : chapterMapByEnrollmentId.entrySet()) {
			Long enrollmentId = chapter.getKey();
			List<ChapterProgress> affectedRows = chapter.getValue();

			CourseEnrollment courseEnrollment = courseEnrollmentService.get(enrollmentId);

			// 受影響 已完成的章節
			List<ChapterProgress> completedRows = completedChapterMapByEnrollmentId.get(enrollmentId);

			// 設定 總章節數 與 已完成章節數（加上 Math.max 防止減到負數）
			int newTotal = Math.max(0, courseEnrollment.getTotalChapters() - affectedRows.size());
			int newCompleted = Math.max(0, courseEnrollment.getCompletedChapters() - completedRows.size());

			// 設定 總章節 數 減去 受影響的章節
			courseEnrollment.setTotalChapters(newTotal);
			// 設定 已完成章節 數 減去 受影響 已完成的章節
			courseEnrollment.setCompletedChapters(newCompleted);
			if (newTotal >= newCompleted) {
				courseEnrollment.setIsChaptersDone(CommonStatusEnum.YES);
			} else {
				courseEnrollment.setIsChaptersDone(CommonStatusEnum.NO);
			}

			courseEnrollments.add(courseEnrollment);
		}

		//遍歷影片 和 測驗的章節
		learningChapters.forEach(e -> {
			switch (e.getContentType()) {
			// 章節類型為影片
			case VIDEO -> {

				// 刪除chaperVideo資料
				chapterVideoService.removeByChapter(e.getCourseChapterId());

				// 刪除sysChunk紀錄
				sysChunkFileService.deleteSysChunkFileByPath(e.getVideoUrl());
				
				// 刪除影片檔案
				if(e.getVideoUrl() != null) {
					String s3Key = s3Helper.extractS3PathInDbUrl(bucketName, e.getVideoUrl());
					s3Helper.removeFileIfPresent(bucketName, s3Key);
				}

			}
			// 章節類型為測驗
			case QUIZ -> {
				// 刪除問卷相關紀錄
				// 1.透過 formId 查詢關於這份表單的所有回覆 ， 並提取responseIds
				List<FormResponse> formResponses = formResponseService.searchSubmissionsByForm(e.getFormId());
				List<Long> responseIds = formResponses.stream().map(FormResponse::getFormResponseId).toList();

				// 2.根據 responseIds 刪除所有answer
				responseAnswerService.removeByResponses(responseIds);

				// 3.根據formId 刪除所有 response
				formResponseService.removeByForm(e.getFormId());

				// 4.刪除表單的欄位
				formFieldService.removeByForm(e.getFormId());

				// 5.刪除表單本身
				formService.removeById(e.getFormId());

			}
			default -> throw new IllegalArgumentException("Unexpected value: " + e.getContentType());
			}
		});

		// 修改 報名課程 總章節數 及 完成章節數
		courseEnrollmentService.saveOrUpdateBatch(courseEnrollments);

		// 刪除 章節觀看時數
		chapterWatchLogService.removeByChapterIds(learningChapterIds);

		// 刪除 章節進度 
		chapterProgressService.removeByChapterIds(learningChapterIds);

		// 最後刪除影響的 課程章節
		courseChapterService.removeBatchByIds(courseChapters);
	}

}
