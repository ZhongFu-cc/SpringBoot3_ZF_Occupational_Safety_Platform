package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.service.SysChunkFileService;

/**
 * <p>
 * 章節影片 - 只有content_type 為video的才有 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-09
 */
@Tag(name = "章節影片(大檔案上傳) API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/chapter-video")
public class ChapterVideoController {

	private final AuthManager authManager;
	private final ChapterVideoService chapterVideoService;
	private final SysChunkFileService sysChunkFileService;

	/** ---------------第二階段 入選後上傳slide、poster、video API ------------------ */

//	@GetMapping("{id}")
//	@Operation(summary = "查詢單一課程Video")
//	@Parameters({
//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
//	public R<List<PaperFileUpload>> getChapterVideos(@PathVariable("id") Long courseChapterId) {
//
//		// 1.根據token 拿取本人的數據
//		SysUserVO sysUserVO = authManager.getUserInfo();
//
//		// 2.透過 paperId 和 memberId 去獲取此稿件在第二階段上傳的所有附件
//		List<PaperFileUpload> secondStagePaperFile = paperService.getSecondStagePaperFile(paperId,
//				memberCache.getMemberId());
//
//		return R.ok(secondStagePaperFile);
//	}
//
//	@GetMapping("check")
//	@Operation(summary = "查看是否已上傳過相同 課程影片")
//	@Parameters({
//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
//	public R<CheckFileVO> slideCheck(@RequestParam String sha256) {
//		// 透過用戶檔案的sha256值，用來判斷是否傳送過，也是達到秒傳的功能
//		CheckFileVO checkFile = sysChunkFileService.checkFile(sha256);
//		return R.ok(checkFile);
//	}
//
//	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	@Operation(summary = "課程影片，檔案分片上傳", description = "請使用formData包裝,兩個key <br>" + "1.data(value = DTO(json))<br>"
//			+ "2.file(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
//			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
//	@Parameters({
//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
//	@SaCheckRole("super-admin")
//	public R<ChunkResponseVO> uploadVideo(@RequestPart("file") MultipartFile file,
//			@RequestPart("data") @Schema(name = "data", implementation = UploadChapterVideoDTO.class) String jsonData)
//			throws JsonMappingException, JsonProcessingException {
//
//		// 將 JSON 字符串轉為對象
//		ObjectMapper objectMapper = new ObjectMapper();
//		UploadChapterVideoDTO uploadChapterVideoDTO = objectMapper.readValue(jsonData, UploadChapterVideoDTO.class);
//
//		// slide分片上傳
//		paperManager.uploadSlideChunk(slideUploadDTO, memberCache.getMemberId(), file);
//
//		return R.ok();
//	}
//
//	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	@Operation(summary = "課程影片，檔案分片上傳更新", description = "請使用formData包裝,兩個key <br>" + "1.data(value = DTO(json))<br>"
//			+ "2.file(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
//			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
//	@Parameters({
//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
//	@SaCheckRole("super-admin")
//	public R<ChunkResponseVO> updateVideo(@RequestPart("file") MultipartFile file,
//			@RequestPart("data") @Schema(name = "data", implementation = PutSlideUploadDTO.class) String jsonData)
//			throws JsonMappingException, JsonProcessingException {
//
//		// 將 JSON 字符串轉為對象
//		ObjectMapper objectMapper = new ObjectMapper();
//		PutSlideUploadDTO slideUpdateDTO = objectMapper.readValue(jsonData, PutSlideUploadDTO.class);
//
//		// slide分片上傳更新
//		paperManager.updateSlideChunk(slideUpdateDTO, memberCache.getMemberId(), file);
//
//		return R.ok();
//	}
//
//	@DeleteMapping("{id}")
//	@Operation(summary = "單一課程影片")
//	@Parameters({
//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
//	@SaCheckRole("super-admin")
//	public R<Void> removeVideo(@PathVariable("id") Long paperId, @RequestParam Long paperFileUploadId) {
//
//		// 透過 paperId 和 memberId 是否為實際投稿者在操作稿件，並透過paperFileId 刪除 第二階段 上傳的附件檔案
//		paperManager.removeSecondStagePaperFile(paperId, memberCache.getMemberId(), paperFileUploadId);
//
//		return R.ok();
//	}

}
