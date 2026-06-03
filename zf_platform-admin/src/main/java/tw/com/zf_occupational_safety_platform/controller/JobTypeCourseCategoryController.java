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
import tw.com.zf_occupational_safety_platform.manager.TypeCategoryManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.TypeCategoryVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseCategoryService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "作業類別 x 課程類別 關聯 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/job-type-course-category")
public class JobTypeCourseCategoryController {

	private final JobTypeCourseCategoryService jobTypeCourseCategoryService;
	private final TypeCategoryManager typeCategoryManager;

	/**
	 * 根據ID 查詢 作業類別 x 課程類別 關聯
	 * 
	 * @param id
	 * @return
	 */
	//	@Operation(summary = "根據ID 查詢 作業類別 x 課程類別 關聯")
	//	@GetMapping("{id}")
	//	public R<JobTypeCourseCategory> getJobType(@PathVariable("id") @Schema(type = "string") Long id) {
	//		JobTypeCourseCategory jobTypeCourseCategory = jobTypeCourseCategoryService.get(id);
	//		return R.ok(jobTypeCourseCategory);
	//	}

	/**
	 * 查詢 作業類別 關聯的 課程類別分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 作業類別 關聯的 課程類別分頁對象")
	public R<IPage<TypeCategoryVO>> findJobTypePage(@RequestParam @Schema(type = "string") Long jobTypeId,
			@RequestParam Integer page, @RequestParam Integer size, @RequestParam(required = false) String queryText) {

		Page<CourseCategory> pageInfo = new Page<>(page, size);
		IPage<TypeCategoryVO> voPage = typeCategoryManager.findCatrgoryPageByJobType(jobTypeId, pageInfo, queryText);
		return R.ok(voPage);
	}

	/**
	 * 新增 作業類別 x 課程類別 關聯
	 * 
	 * @param addTypeCategoryDTO
	 * @return
	 */
	@Operation(summary = "新增 作業類別 x 課程類別 關聯")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> addAssociation(@RequestBody @Valid AddTypeCategoryDTO addTypeCategoryDTO) {
		jobTypeCourseCategoryService.add(addTypeCategoryDTO);
		return R.ok();
	}

	/**
	 * 更新 作業類別 x 課程類別 關聯的 必要欄位
	 * 
	 * @param putTypeCategoryDTO
	 * @return
	 */
	@Operation(summary = "更新 作業類別 x 課程類別 關聯的 必要欄位")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateMandatory(@RequestBody @Valid PutTypeCategoryDTO putTypeCategoryDTO) {
		jobTypeCourseCategoryService.update(putTypeCategoryDTO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 作業類別 x 課程類別 關聯
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 作業類別 x 課程類別 關聯 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeAssociation(@PathVariable @Schema(type = "string") Long id) {
		jobTypeCourseCategoryService.remove(id);
		return R.ok();
	}

}
