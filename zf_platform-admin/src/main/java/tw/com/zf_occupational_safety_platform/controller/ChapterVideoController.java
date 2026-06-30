package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.ChapterVideoManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.UpdateChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.UploadChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.CheckFileVO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.ChunkResponseVO;
import tw.com.zf_occupational_safety_platform.system.service.SysChunkFileService;
import tw.com.zf_occupational_safety_platform.utils.R;

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
	private final ChapterVideoManager chapterVideoManager;
	private final ChapterVideoService chapterVideoService;
	private final SysChunkFileService sysChunkFileService;

	/** ---------------第二階段 入選後上傳slide、poster、video API ------------------ */

	@GetMapping("{id}")
	@Operation(summary = "查詢單一課程Video")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckLogin
	public R<ChapterVideo> getChapterVideos(@PathVariable("id") Long courseChapterId) {
		ChapterVideo chapterVideo = chapterVideoService.getByChapter(courseChapterId);
		return R.ok(chapterVideo);
	}

	@GetMapping("check")
	@Operation(summary = "查看是否已上傳過相同 課程影片，如果有則將此章節的影片地址轉換到已有影片位置")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	public R<Void> slideCheck(@RequestParam String sha256) {
		chapterVideoManager.checkFile(sha256);
		return R.ok();
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "課程影片，檔案分片上傳", description = "請使用formData包裝,兩個key <br>" + "1.data(value = DTO(json))<br>"
			+ "2.file(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	public R<ChunkResponseVO> uploadVideo(@RequestPart("file") MultipartFile file,
			@RequestPart("data") @Schema(name = "data", implementation = UploadChapterVideoDTO.class) String jsonData)
			throws JsonMappingException, JsonProcessingException {

		// 將 JSON 字符串轉為對象
		ObjectMapper objectMapper = new ObjectMapper();
		UploadChapterVideoDTO uploadChapterVideoDTO = objectMapper.readValue(jsonData, UploadChapterVideoDTO.class);

		// 分片上傳
		chapterVideoManager.uploadChunk(uploadChapterVideoDTO, file);

		return R.ok();
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "課程影片，檔案分片上傳更新", description = "請使用formData包裝,兩個key <br>" + "1.data(value = DTO(json))<br>"
			+ "2.file(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	public R<ChunkResponseVO> updateVideo(@RequestPart("file") MultipartFile file,
			@RequestPart("data") @Schema(name = "data", implementation = UpdateChapterVideoDTO.class) String jsonData)
			throws JsonMappingException, JsonProcessingException {

		// 將 JSON 字符串轉為對象
		ObjectMapper objectMapper = new ObjectMapper();
		UpdateChapterVideoDTO updateChapterVideoDTO = objectMapper.readValue(jsonData, UpdateChapterVideoDTO.class);

		// 分片上傳更新
		chapterVideoManager.updateChunk(updateChapterVideoDTO, file);

		return R.ok();
	}

	@DeleteMapping("{id}")
	@Operation(summary = "刪除，單一課程影片")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	public R<Void> removeVideo(@PathVariable("id") Long chapterVideoId) {
		chapterVideoManager.removeFile(chapterVideoId);
		return R.ok();
	}

}
