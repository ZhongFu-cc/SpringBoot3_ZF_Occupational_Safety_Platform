package tw.com.zf_occupational_safety_platform.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.FormConvert;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.pojo.DTO.FormFieldOptionDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormFieldVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Form;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormResponse;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.FormFieldService;
import tw.com.zf_occupational_safety_platform.service.FormResponseService;
import tw.com.zf_occupational_safety_platform.service.FormService;
import tw.com.zf_occupational_safety_platform.service.ResponseAnswerService;

/**
 * 
 */
@Component
@RequiredArgsConstructor
public class FormManager {

	private final ObjectMapper objectMapper;

	private final FormConvert formConvert;
	private final FormService formService;
	private final FormFieldService formFieldService;
	private final FormResponseService formResponseService;
	private final ResponseAnswerService responseAnswerService;

	private final ChapterProgressService chapterProgressService;
	private final CourseEnrollmentService courseEnrollmentService;
	private final CourseChapterService courseChapterService;

	/**
	 * 
	 * @param formId
	 * @return
	 */
	public FormVO getFillableForm(Long formId) {

		// 1.查詢要填寫的表單
		Form form = formService.searchForm(formId);

		// 2.轉換資料
		FormVO formVO = formConvert.entityToVO(form);

		// 3.根據 formId 查詢表單 及其 欄位
		List<FormFieldVO> formFieldVOList = formFieldService.searchFormStructureByForm(formId);

		// 4.VO填充欄位
		formVO.setFormFields(formFieldVOList);

		return formVO;
	}

	/**
	 * 獲得指定數量 總測試 考卷<br>
	 * 順序 及 選項打亂
	 * 
	 * @param enrollmentId
	 * @param formId
	 * @param count
	 * @return
	 */
	public FormVO getRandomQuizForm(Long enrollmentId, Long formId, int count) {

		// 判斷此章節已經完成到可以進行測驗
		CourseEnrollment courseEnrollment = courseEnrollmentService.get(enrollmentId);
		if (courseEnrollment == null) {
			throw new CourseException("無此資料");
		}

		if (!CourseStatusEnum.IN_PROGRESS.equals(courseEnrollment.getStatus())) {
			throw new CourseException("課程必須處於 學習中 才可以進行測驗");
		}

		Integer totalChapters = courseEnrollment.getTotalChapters();
		Integer completedChapters = courseEnrollment.getCompletedChapters();

		if (!courseEnrollment.getIsMinutesMet().getBooleanValue() || ((totalChapters - 1) != completedChapters)) {
			throw new CourseException("尚未達成測驗的條件");
		}

		Form form = formService.searchForm(formId);
		FormVO formVO = formConvert.entityToVO(form);

		List<FormFieldVO> formFieldVOList = formFieldService.searchFormStructureByForm(formId);

		Collections.shuffle(formFieldVOList);

		List<FormFieldVO> randomQuestions = formFieldVOList.stream()
				.limit(Math.min(count, formFieldVOList.size()))
				.map(this::shuffleChoicesSafely)
				.toList();

		formVO.setFormFields(randomQuestions);
		return formVO;
	}

	/**
	 * 深層複製
	 * 
	 * @param source
	 * @return
	 */
	private FormFieldVO deepCopy(FormFieldVO source) {
		try {
			return objectMapper.readValue(objectMapper.writeValueAsBytes(source), FormFieldVO.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 深層複製並打亂
	 * 
	 * @param field
	 * @return
	 */
	private FormFieldVO shuffleChoicesSafely(FormFieldVO field) {

		FormFieldVO copy = deepCopy(field);

		if (copy.getOptions() == null || copy.getOptions().getChoices() == null) {
			return copy;
		}

		List<FormFieldOptionDTO.Choice> choices = new ArrayList<>(copy.getOptions().getChoices());
		Collections.shuffle(choices);
		copy.getOptions().setChoices(choices);

		return copy;
	}

	public void deleteForm(Long formId) {
		// 1.透過 formId 查詢關於這份表單的所有回覆 ， 並提取responseIds
		List<FormResponse> formResponses = formResponseService.searchSubmissionsByForm(formId);
		List<Long> responseIds = formResponses.stream().map(FormResponse::getFormResponseId).toList();

		// 2.根據 responseIds 刪除所有answer
		responseAnswerService.removeByResponses(responseIds);

		// 3.根據formId 刪除所有 response
		formResponseService.removeByForm(formId);

		// 4.刪除表單的欄位
		formFieldService.removeByForm(formId);

		// 5.刪除表單本身
		formService.removeById(formId);

	}

}
