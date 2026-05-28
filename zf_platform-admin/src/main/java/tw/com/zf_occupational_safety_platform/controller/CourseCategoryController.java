package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
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

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.service.CourseCategoryService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 課程類別 與 法規限制表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Tag(name = "課程類別 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/course-category")
public class CourseCategoryController {

	private final CourseCategoryService courseCategoryService;

	/**
	 * 根據ID 查詢 課程類別
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 課程類別")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@GetMapping("{id}")
	public R<CourseCategory> getCourseCategory(@PathVariable("id") @Schema(type = "string") Long id) {
		CourseCategory courseCategory = courseCategoryService.get(id);
		return R.ok(courseCategory);
	}

	/**
	 * 查詢 課程類別分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 課程類別分頁對象")
	public R<IPage<CourseCategory>> findCourseCategoryPage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {

		Page<CourseCategory> pageInfo = new Page<>(page, size);
		IPage<CourseCategory> courseCategoryPage = courseCategoryService.findPageByQuery(pageInfo, queryText);
		return R.ok(courseCategoryPage);
	}

	/**
	 * 新增 課程類別
	 * 
	 * @param addCourseCategoryDTO
	 * @return
	 */
	@Operation(summary = "新增 課程類別")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> saveCourseCategory(@RequestBody @Valid AddCourseCategoryDTO addCourseCategoryDTO) {
		courseCategoryService.create(addCourseCategoryDTO);
		return R.ok();
	}

	/**
	 * 更新 課程類別
	 * 
	 * @param putCourseCategoryDTO
	 * @return
	 */
	@Operation(summary = "更新 課程類別")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateCourseCategory(@RequestBody @Valid PutCourseCategoryDTO putCourseCategoryDTO) {
		courseCategoryService.update(putCourseCategoryDTO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 課程類別
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 課程類別 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeCourseCategory(@PathVariable @Schema(type = "string") Long id) {
		courseCategoryService.remove(id);
		return R.ok();
	}

}
