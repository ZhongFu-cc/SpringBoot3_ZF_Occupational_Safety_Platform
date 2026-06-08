package tw.com.zf_occupational_safety_platform.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.FormConvert;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormFieldVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Form;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormResponse;
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

	private final FormConvert formConvert;
	private final FormService formService;
	private final FormFieldService formFieldService;
	private final FormResponseService formResponseService;
	private final ResponseAnswerService responseAnswerService;

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
	 * 獲得隨機數量 單元測試 表單
	 * 
	 * @param formId
	 * @param count
	 * @return
	 */
	public FormVO getRandomQuizForm(Long formId, int count) {
		// 1.查詢要填寫的表單
		Form form = formService.searchForm(formId);

		// 2.轉換資料
		FormVO formVO = formConvert.entityToVO(form);

		// 3.根據 formId 查詢表單 及其 欄位
		List<FormFieldVO> formFieldVOList = formFieldService.searchFormStructureByForm(formId);

		// 4.打亂列表
		Collections.shuffle(formFieldVOList);

		// 5.從打亂過的列表，再抽取限制數量的題目，最後放回表單
		List<FormFieldVO> randomQuestions = formFieldVOList.stream()
				.limit(Math.min(count, formFieldVOList.size()))
				.toList();
		formVO.setFormFields(randomQuestions);
		return formVO;
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
