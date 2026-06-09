package tw.com.zf_occupational_safety_platform.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.FormResponseManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.QuizResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddFormResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutFormResponseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.AnswerResultVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormResponseVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.FormVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.FormResponse;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 表單回覆紀錄 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2025-12-23
 */
@RestController
@RequestMapping("/form-response")
@RequiredArgsConstructor
public class FormResponseController {

	private final AuthManager authManager;
	private final FormResponseManager formResponseManager;

	@GetMapping("{id}")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 「要修改」 表單回覆 , 包含表單欄位 及 之前填寫數據")
	public R<FormVO> getEditableForm(@PathVariable("id") Long responseId) {
		return R.ok(formResponseManager.getEditableForm(responseId));
	}

	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢表單分頁對象")
	public R<IPage<FormResponseVO>> getFormResponsePage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam Long formId) {

		Page<FormResponse> pageInfo = new Page<>(page, size);

		IPage<FormResponseVO> responsesPage = formResponseManager.searchResponsesPage(pageInfo, formId);
		return R.ok(responsesPage);
	}

	@PostMapping("answering-quiz")
	@Operation(summary = "單元測試作答")
	@Parameters({
			@Parameter(name = "Authorization-member", description = "請求頭token,token-value開頭必須為Bearer ", required = false, in = ParameterIn.HEADER) })
	@SaCheckLogin
	public R<AnswerResultVO> answeringQuiz(@RequestBody @Valid QuizResponseDTO quizResponseDTO) {

		// 1.初始化memberId
		Long memberId = null;

		// 2.如果有傳token , 且是有在Redis中紀錄的登入狀態,會拿到loginId , 業務上來說也是memberId
		SysUserVO userInfo = authManager.getUserInfo();
		memberId = userInfo.getSysUserId();

		// 3.不論memberId是否有值,都放進DTO中
		quizResponseDTO.setMemberId(memberId);

		// 4.調用表單回覆新增
		AnswerResultVO answerResultVO = formResponseManager.quizResponse(quizResponseDTO,userInfo);

		return R.ok(answerResultVO);
	}

	@PostMapping
	@Operation(summary = "新增單一表單回覆")
	@Parameters({
			@Parameter(name = "Authorization-member", description = "請求頭token,token-value開頭必須為Bearer ", required = false, in = ParameterIn.HEADER) })
	public R<Void> saveFormResponse(@RequestBody @Valid AddFormResponseDTO formResponseDTO) {

		// 1.初始化memberId
		Long memberId = null;

		// 2.如果有傳token , 且是有在Redis中紀錄的登入狀態,會拿到loginId , 業務上來說也是memberId
		SysUserVO userInfo = authManager.getUserInfo();
		memberId = userInfo.getSysUserId();

		// 3.不論memberId是否有值,都放進DTO中
		formResponseDTO.setMemberId(memberId);

		// 4.調用表單回覆新增
		formResponseManager.addFormResponse(formResponseDTO);

		return R.ok();
	}

	@PutMapping
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "修改 單一表單回覆，只給管理者修改")
	public R<Void> updateFormResponse(@RequestBody @Valid PutFormResponseDTO putFormResponseDTO) {
		formResponseManager.updateFormResponse(putFormResponseDTO);
		return R.ok();

	}

	@DeleteMapping("{id}")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "刪除 單一表單回覆，只給管理者刪除")
	public R<Void> deleteFormResponse(@PathVariable("id") Long formResponseId) {
		formResponseManager.deleteFormResponse(formResponseId);
		return R.ok();
	}

	@GetMapping("get-download-url")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "獲取,某個表單回覆 Excel 下載URL")
	public R<String> getDownloadUrl(Long formId) throws IOException {
		return R.ok("操作成功", "/form-response/download-excel?formId=" + formId);
	}

	@GetMapping("download-excel")
	@Operation(summary = "下載某個表單的 Excel 所有回覆")
	public void downloadFormResponseExcel(HttpServletResponse response, Long formId) throws IOException {
		formResponseManager.downloadFormResponseExcel(response, formId);
	}

}
