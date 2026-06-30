package tw.com.zf_occupational_safety_platform.manager;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.FormConvert;
import tw.com.zf_occupational_safety_platform.convert.FormFieldConvert;
import tw.com.zf_occupational_safety_platform.convert.FormResponseConvert;
import tw.com.zf_occupational_safety_platform.convert.ResponseAnswerConvert;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.FormStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.FormException;
import tw.com.zf_occupational_safety_platform.pojo.BO.ResponseAnswerMatrixBO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.FormFieldOptionDTO.Choice;
import tw.com.zf_occupational_safety_platform.pojo.DTO.QuizResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddFormResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddResponseAnswerDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutFormResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutResponseAnswerDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.AnswerResultVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormFieldVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormResponseVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Form;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormField;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormResponse;
import tw.com.zf_occupational_safety_platform.pojo.entity.ResponseAnswer;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.FormFieldService;
import tw.com.zf_occupational_safety_platform.service.FormResponseService;
import tw.com.zf_occupational_safety_platform.service.FormService;
import tw.com.zf_occupational_safety_platform.service.ResponseAnswerService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

@Component
@RequiredArgsConstructor
public class FormResponseManager {

	private final FormConvert formConvert;
	private final FormResponseConvert formResponseConvert;
	private final ResponseAnswerConvert responseAnswerConvert;
	private final FormFieldConvert formFieldConvert;

	private final FormService formService;
	private final FormFieldService formFieldService;
	private final FormResponseService formResponseService;
	private final ResponseAnswerService responseAnswerService;

	private final CourseEnrollmentService courseEnrollmentService;
	private final ChapterProgressService chapterProgressService;

	/**
	 * 獲取 可編輯的 表單對象
	 * 
	 * @param responseId
	 * @return
	 */
	public FormVO getEditableForm(Long responseId) {

		// 1.查詢此次表單回覆
		FormResponse formResponse = formResponseService.getById(responseId);

		// 2.查詢要填寫的表單
		Form form = formService.searchForm(formResponse.getFormId());

		// 3.轉換資料
		FormVO formVO = formConvert.entityToVO(form);

		// 4.根據 formId 查詢表單 及其 欄位
		List<FormFieldVO> formFieldVOList = formFieldService.searchFormStructureByForm(form.getFormId());

		// 5.根據 responseId 查詢已填寫的結果映射
		Map<Long, ResponseAnswer> responseAnswerMap = responseAnswerService.searchAnswerMapByFieldId(responseId);

		// 6.遍歷formFieldVO , 把PutResponseAnswerDTO組裝進answer中
		List<FormFieldVO> processedFormFieldVOList = formFieldVOList.stream().map(vo -> {
			ResponseAnswer responseAnswer = responseAnswerMap.get(vo.getFormFieldId());
			PutResponseAnswerDTO answer = responseAnswerConvert.entityToPutDTO(responseAnswer);
			vo.setAnswer(answer);

			return vo;
		}).toList();

		// 7.VO填充欄位
		formVO.setFormFields(processedFormFieldVOList);

		return formVO;

	}

	/**
	 * 
	 * @param pageInfo
	 * @param formId
	 * @return
	 */
	public IPage<FormResponseVO> searchResponsesPage(IPage<FormResponse> pageInfo, Long formId) {

		// 根據 formId 查詢表單 及其 欄位
		List<FormFieldVO> formFieldVOList = formFieldService.searchFormStructureByForm(formId);

		IPage<FormResponse> formResponses = formResponseService.searchSubmissionsByForm(pageInfo, formId);

		List<FormResponseVO> responseVOList = formResponses.getRecords().stream().map(response -> {
			FormResponseVO responseVO = formResponseConvert.entityToVO(response);

			// 每次都複製一份全新的欄位列表
			List<FormFieldVO> formFieldCopyList = formFieldVOList.stream().map(original -> {
				FormFieldVO copy = new FormFieldVO();
				BeanUtils.copyProperties(original, copy);
				return copy;
			}).toList();

			Map<Long, ResponseAnswer> answerMap = responseAnswerService
					.searchAnswerMapByFieldId(response.getFormResponseId());

			// 這次只改這份複本
			formFieldCopyList.forEach(fieldVO -> {
				ResponseAnswer answerEntity = answerMap.get(fieldVO.getFormFieldId());
				PutResponseAnswerDTO dto = responseAnswerConvert.entityToPutDTO(answerEntity);
				fieldVO.setAnswer(dto); // 只改複本
			});

			responseVO.setFormFields(formFieldCopyList);
			return responseVO;
		}).toList();

		IPage<FormResponseVO> resultPage = new Page<>(formResponses.getCurrent(), formResponses.getSize(),
				formResponses.getTotal());
		resultPage.setRecords(responseVOList);

		return resultPage;

	}

	/**
	 * 總測驗 的 測驗回覆
	 * 
	 * @param quizResponseDTO
	 */
	public List<AnswerResultVO> quizResponse(QuizResponseDTO quizResponseDTO, SysUserVO operator) {
		// 1.表單回覆進來,先查詢表單基本資訊
		Form form = formService.searchForm(quizResponseDTO.getFormId());

		// 2.如果表單要求登入 , 但是memberId沒傳遞則代表不合規
		if (CommonStatusEnum.YES.getValue().equals(form.getRequireLogin()) && quizResponseDTO.getMemberId() == null) {
			throw new FormException("此表單填寫需先登入");
		}

		// 3.判斷表單是否開放
		if (!FormStatusEnum.PUBLISHED.equals(form.getStatus())) {
			throw new FormException("表單不處於發佈狀態");
		}

		// 4.如果不允許重複填寫 , 根據 memberId 查詢是否有回覆紀錄 
		if (CommonStatusEnum.NO.getValue().equals(form.getAllowMultipleSubmissions())
				&& quizResponseDTO.getMemberId() != null) {
			List<FormResponse> formResponses = formResponseService
					.searchSubmissionsByMember(quizResponseDTO.getFormId(), quizResponseDTO.getMemberId());
			if (formResponses.size() > 0) {
				throw new FormException("表單已填寫過");
			}

		}

		// 5. 判斷是否為本人的章節
		ChapterProgress chapterProgress = chapterProgressService.getByOwner(quizResponseDTO.getChapterProgressId(),
				operator.getSysUserId());
		if (chapterProgress == null) {
			throw new FormException("不屬於本人的學習章節");
		}

		// 5.先輸入這筆表單回覆,拿到表單回覆ID
		FormResponse formResponse = formResponseService.submit(quizResponseDTO);
		Map<Long, FormField> mapByFieldId = formFieldService.findMapByFieldId(quizResponseDTO.getFormId());

		// 初始化作答回覆 和 作答的結果
		List<ResponseAnswer> responseAnswerList = new ArrayList<>(quizResponseDTO.getResponseAnswer().size());
		List<AnswerResultVO> answerResultVOList = new ArrayList<>(quizResponseDTO.getResponseAnswer().size());

		// 6.遍歷這次的作答
		for (AddResponseAnswerDTO addResponseAnswerDTO : quizResponseDTO.getResponseAnswer()) {

			ResponseAnswer responseAnswer = responseAnswerConvert.addDTOToEntity(addResponseAnswerDTO);
			// 塞入本次表單回覆ID
			responseAnswer.setFormResponseId(formResponse.getFormResponseId());
			// 創建作答結果VO
			AnswerResultVO answerResultVO = new AnswerResultVO();

			// 批改：找到對應題目，比對正確答案
			FormField field = mapByFieldId.get(responseAnswer.getFormFieldId());
			if (field != null) {
				FormFieldVO fieldVO = formFieldConvert.entityToVO(field);
				fieldVO.getOptions()
						.getChoices()
						.stream()
						.filter(Choice::isCorrectAnswer)
						.findFirst()
						.ifPresent(correct -> {
							// 填入作答結果
							answerResultVO.setQuestion(fieldVO.getLabel());
							answerResultVO.setCorrectAnswer(correct.getLabel());
							answerResultVO.setYourAnswer(responseAnswer.getAnswerValue());

							boolean isCorrect = correct.getId().equals(responseAnswer.getChoiceId());
							// 提取回答次數 , +1後重設置
							Integer quizAttempts = chapterProgress.getQuizAttempts();
							quizAttempts = quizAttempts == null ? 0 : quizAttempts;
							quizAttempts += 1;
							chapterProgress.setQuizAttempts(quizAttempts);

							// 當回答正確
							if (isCorrect) {
								responseAnswer.setIsCorrectAnswer(CommonStatusEnum.YES);
								answerResultVO.setIsCorrect(CommonStatusEnum.YES);
							} else {
								// 當回答錯誤
								responseAnswer.setIsCorrectAnswer(CommonStatusEnum.NO);
								answerResultVO.setIsCorrect(CommonStatusEnum.NO);
							}

						});

			}

			// 為兩個列表添加Item
			answerResultVOList.add(answerResultVO);
			responseAnswerList.add(responseAnswer);

		}

		// 儲存作答結果
		responseAnswerService.saveBatch(responseAnswerList);

		// 當測驗全通過
		long count = answerResultVOList.stream().filter(e -> e.getIsCorrect().getBooleanValue()).count();
		if (count == 10) {
			// 設定章節測驗通過 , 100分 , 完成 , 設置完成時間
			chapterProgress.setIsQuizPassed(CommonStatusEnum.YES);
			chapterProgress.setQuizScore(100);
			chapterProgress.setStatus(CourseStatusEnum.COMPLETED);
			chapterProgress.setCompletedAt(LocalDateTime.now());

			// 章節通過，同步更新課程報名的整體學習狀況
			CourseEnrollment courseEnrollment = courseEnrollmentService.get(chapterProgress.getCourseEnrollmentId());
			// 同時更新 課程完成的整體狀態
			Integer totalChapters = courseEnrollment.getTotalChapters();
			Integer completedChapters = courseEnrollment.getCompletedChapters();
			completedChapters += 1;
			// 當完成課程章節數 大於等於 總共課程章節數
			if (completedChapters >= totalChapters) {
				courseEnrollment.setCompletedChapters(totalChapters);
				courseEnrollment.setIsChaptersDone(CommonStatusEnum.YES);
			} else {
				// 當完成課程章節數 大於等於 總共課程章節數
				courseEnrollment.setCompletedChapters(completedChapters);
			}
			courseEnrollmentService.updateById(courseEnrollment);

		} else {
			// 設定章節測驗失敗 , 設定分數 , 進行中
			chapterProgress.setQuizScore(Math.toIntExact(count * 10));
			chapterProgress.setStatus(CourseStatusEnum.IN_PROGRESS);
		}

		// 更新課程章節進度
		chapterProgressService.updateById(chapterProgress);

		// 返回作答結果
		return answerResultVOList;

	}

	/**
	 * 新增 表單回覆 及 回覆細項
	 * 
	 * @param formResponseDTO
	 */
	public void addFormResponse(AddFormResponseDTO formResponseDTO) {

		// 1.表單回覆進來,先查詢表單基本資訊
		Form form = formService.searchForm(formResponseDTO.getFormId());

		// 2.如果表單要求登入 , 但是memberId沒傳遞則代表不合規
		if (CommonStatusEnum.YES.getValue().equals(form.getRequireLogin()) && formResponseDTO.getMemberId() == null) {
			throw new FormException("此表單填寫需先登入");
		}

		// 3.判斷表單是否開放
		if (!FormStatusEnum.PUBLISHED.equals(form.getStatus())) {
			throw new FormException("表單不處於發佈狀態");
		}

		// 4.如果不允許重複填寫 , 根據 memberId 查詢是否有回覆紀錄 
		if (CommonStatusEnum.NO.getValue().equals(form.getAllowMultipleSubmissions())
				&& formResponseDTO.getMemberId() != null) {
			List<FormResponse> formResponses = formResponseService
					.searchSubmissionsByMember(formResponseDTO.getFormId(), formResponseDTO.getMemberId());
			if (formResponses.size() > 0) {
				throw new FormException("表單已填寫過");
			}

		}

		// 5.先輸入這筆表單回覆,拿到表單回覆ID
		FormResponse formResponse = formResponseService.submit(formResponseDTO);

		Map<Long, FormField> mapByFieldId = formFieldService.findMapByFieldId(formResponseDTO.getFormId());

		// 6.拿到回答結果,進行轉換,最終拿到詳細回覆結果
		List<ResponseAnswer> responseAnswerList = formResponseDTO.getResponseAnswerList()
				.stream()
				.map(responseAnswerDTO -> {

					// 轉換
					ResponseAnswer responseAnswer = responseAnswerConvert.addDTOToEntity(responseAnswerDTO);
					// 塞入本次表單回覆ID
					responseAnswer.setFormResponseId(formResponse.getFormResponseId());

					// 批改：找到對應題目，比對正確答案
					FormField field = mapByFieldId.get(responseAnswerDTO.getFormFieldId());
					if (field != null) {
						FormFieldVO fieldVO = formFieldConvert.entityToVO(field);
						fieldVO.getOptions()
								.getChoices()
								.stream()
								.filter(Choice::isCorrectAnswer)
								.findFirst()
								.ifPresent(correct -> {
									boolean isCorrect = correct.getId().equals(responseAnswerDTO.getChoiceId());
									if (isCorrect) {
										responseAnswer.setIsCorrectAnswer(CommonStatusEnum.YES);
									} else {
										responseAnswer.setIsCorrectAnswer(CommonStatusEnum.NO);
									}
								});

					}

					return responseAnswer;

				})
				.toList();

		// 7.批量插入
		responseAnswerService.saveBatch(responseAnswerList);

		// 8.

	}

	/**
	 * 更新表單回覆
	 * 
	 * @param putResponseAnswerDTOList
	 */
	public void updateFormResponse(PutFormResponseDTO putFormResponseDTO) {

		// 1. 從列表（responseAnswerList）進行分組
		// 注意：這裡是用 list.stream()，不是用 dto 本身 stream
		Map<Boolean, List<PutResponseAnswerDTO>> partitionedMap = putFormResponseDTO.getResponseAnswerList()
				.stream()
				.collect(Collectors.partitioningBy(dto -> dto.getResponseAnswerId() != null));

		// 2. 處理「更新」的部分 (Update)
		List<PutResponseAnswerDTO> updateDTOs = partitionedMap.get(true);
		if (!updateDTOs.isEmpty()) {
			List<ResponseAnswer> updateEntities = updateDTOs.stream()
					.map(responseAnswerConvert::putDTOToEntity)
					.toList();
			responseAnswerService.updateBatchById(updateEntities);
		}

		// 3. 處理「新增/補填」的部分 (Insert)
		List<PutResponseAnswerDTO> insertDTOs = partitionedMap.get(false);
		if (!insertDTOs.isEmpty()) {
			List<ResponseAnswer> insertEntities = insertDTOs.stream().map(dto -> {
				ResponseAnswer responseAnswer = responseAnswerConvert.putDTOToEntity(dto);
				responseAnswer.setFormResponseId(putFormResponseDTO.getFormResponseId());
				return responseAnswer;
			}).toList();
			responseAnswerService.saveBatch(insertEntities);
		}

	}

	/**
	 * 刪除表單回覆
	 * 
	 * @param formResponseId
	 */
	public void deleteFormResponse(Long formResponseId) {

		// 1.先刪除表單內所有的回覆細項
		responseAnswerService.removeByResponse(formResponseId);

		// 2.再刪除表單回覆本身
		formResponseService.removeById(formResponseId);

	}

	/**
	 * 下載表單回覆的Excel
	 *
	 * @param response
	 * @param formId
	 * @throws IOException
	 */
	public void downloadFormResponseExcel(HttpServletResponse response, Long formId) throws IOException {

		// 1.獲取表單基本資訊
		Form form = formService.searchForm(formId);

		// 2.拿到這張表單所有的問題,並過濾掉不須輸出到excel的部分
		List<FormFieldVO> formFieldVOs = formFieldService.searchFormStructureByForm(formId);
		List<FormFieldVO> exportFields = formFieldVOs.stream().filter(f -> f.getFieldType().isExportable()).toList();

		// 是否需要顯示會員資訊欄位（依據表單的 requireLogin 設定）
		boolean needMemberInfo = form.getRequireLogin() != null
				&& CommonStatusEnum.YES.getValue().equals(form.getRequireLogin());

		// 3. 構建表頭
		List<List<String>> head = new ArrayList<>();

		// 3-1. 動態問題標題
		exportFields.forEach(field -> head.add(Collections.singletonList(field.getLabel())));

		// 3-2. 根據設定加入會員欄位
		if (needMemberInfo) {
			head.add(Collections.singletonList("Member ID"));
			head.add(Collections.singletonList("Member Name"));
		}

		// 3-3. 固定時間欄位
		head.add(Collections.singletonList("填單時間"));
		head.add(Collections.singletonList("更新時間"));

		// 4.拿到此表單的所有回覆
		List<FormResponse> responseList = formResponseService.searchSubmissionsByForm(formId);
		List<Long> responseIds = responseList.stream().map(FormResponse::getFormResponseId).toList();

		// 5.透過Ids 獲取答案矩陣
		ResponseAnswerMatrixBO matrixBO = responseAnswerService.searchResponseAnswerMatrixBO(responseIds);

		// 6. 構建內容數據
		List<List<Object>> dataRows = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		for (FormResponse res : responseList) {
			List<Object> rowData = new ArrayList<>();
			Long resId = res.getFormResponseId();

			// 6-1. 動態問題答案
			for (FormFieldVO field : exportFields) {
				rowData.add(matrixBO.getAnswer(resId, field.getFormFieldId()));
			}

			// 6-2. 會員資訊（只有需要時才加入）
			//			if (needMemberInfo) {
			//				Long memberId = res.getMemberId();
			//				String memberName = Optional.ofNullable(memberId)
			//						.map(memberService::getById) // 如果 memberId 不為 null，查詢 member
			//						.map(memberService::getOnlyMemberName) // 如果 member 不為 null，計算姓名
			//						.orElse(""); // 否則回空字串
			//
			//				rowData.add(memberId != null ? memberId.toString() : "");
			//				rowData.add(memberName);
			//			}

			// 6-3. 時間資訊
			rowData.add(res.getCreateDate() != null ? res.getCreateDate().format(formatter) : "");
			rowData.add(res.getUpdateDate() != null ? res.getUpdateDate().format(formatter) : "");

			dataRows.add(rowData);
		}

		// 7. 設置 Http Header（檔名部分不變）
		String rawFileName = "問卷表單_" + form.getTitle();
		String encodedFileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(rawFileName, "ISO-8859-1") + ".xlsx\"; "
						+ "filename*=UTF-8''" + encodedFileName + ".xlsx");

		// 8. 使用 EasyExcel 輸出
		EasyExcel.write(response.getOutputStream())
				.head(head)
				.automaticMergeHead(false)
				.sheet("回覆結果")
				.doWrite(dataRows);
	}

}
