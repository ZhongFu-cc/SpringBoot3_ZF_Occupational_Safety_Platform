package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.CourseManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 課程主表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Tag(name = "課程主表 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

	private final CourseService courseService;
	private final CourseManager courseManager;

	/**
	 * 根據ID 查詢 課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@GetMapping("{id}")
	public R<Course> getCourse(@PathVariable("id") @Schema(type="string") Long id) {
		Course course = courseService.get(id);
		return R.ok(course);
	}

	/**
	 * 查詢 課程分頁對象
	 * 
	 * @param page
	 * @param size
	 * @param courseCategoryId 課程類別ID
	 * @param queryText        查詢輸入
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 課程分頁對象")
	public R<IPage<Course>> findCoursePage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) @Schema(description = "課程類別ID") Long courseCategoryId,
			@RequestParam(required = false) @Schema(description = "查詢輸入") String queryText) {

		Page<Course> pageInfo = new Page<>(page, size);
		IPage<Course> coursePage = courseService.findPageByQuery(pageInfo, courseCategoryId, queryText);
		return R.ok(coursePage);
	}

	/**
	 * 新增 課程
	 * 
	 * @param addCourseDTO
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "新增 課程", description = "請使用formData包裝,2個key <br>" + "1.data(value = DTO(json))<br>"
			+ "2.縮圖檔案 imgFile(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	public R<Void> saveCourse(
			@RequestPart(value = "imgFile", required = false) MultipartFile imgFile,
			@RequestPart("data") @Schema(name = "data", implementation = AddCourseDTO.class) String jsonData)
			throws JsonMappingException, JsonProcessingException {

		// 將 JSON 字符串轉為對象
		ObjectMapper objectMapper = new ObjectMapper();
		AddCourseDTO addCourseDTO = objectMapper.readValue(jsonData, AddCourseDTO.class);

		// 將檔案和資料對象傳給後端
		courseManager.createCourse(addCourseDTO, imgFile);
		return R.ok();
	}

	/**
	 * 更新 課程
	 * 
	 * @param putCourseDTO
	 * @return
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 */
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "更新 課程", description = "請使用formData包裝,2個key <br>" + "1.data(value = DTO(json))<br>"
			+ "2.縮圖檔案 imgFile(value = binary)<br>" + "knife4j Web 文檔顯示有問題, 真實傳輸方式為 「multipart/form-data」<br>"
			+ "請用 http://localhost:8080/swagger-ui/index.html 測試 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	public R<Void> updateCourse(@RequestPart(value = "imgFile", required = false) MultipartFile imgFile,
			@RequestPart("data") @Schema(name = "data", implementation = PutCourseDTO.class) String jsonData)
			throws JsonMappingException, JsonProcessingException {

		// 將 JSON 字符串轉為對象
		ObjectMapper objectMapper = new ObjectMapper();
		PutCourseDTO putCourseDTO = objectMapper.readValue(jsonData, PutCourseDTO.class);

		courseManager.updateCourse(putCourseDTO, imgFile);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 課程 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeCourse(@PathVariable @Schema(type="string") Long id) {
		courseService.remove(id);
		return R.ok();
	}

}
