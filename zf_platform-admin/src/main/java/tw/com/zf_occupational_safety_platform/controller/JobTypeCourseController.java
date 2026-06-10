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
import tw.com.zf_occupational_safety_platform.manager.JobCourseManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.JobCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "作業類別 x 課程 關聯 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/job-type-course")
public class JobTypeCourseController {

	private final JobTypeCourseService jobTypeCourseService;
	private final JobCourseManager jobCourseManager;

	/**
	 * 根據ID 查詢 作業類別 x 課程類別 關聯
	 * 
	 * @param id
	 * @return
	 */
	//	@Operation(summary = "根據ID 查詢 作業類別 x 課程類別 關聯")
	//	@GetMapping("{id}")
	//	public R<JobTypeCourse> getJobType(@PathVariable("id") @Schema(type = "string") Long id) {
	//		JobTypeCourse jobTypeCourseCategory = jobTypeCourseCategoryService.get(id);
	//		return R.ok(jobTypeCourseCategory);
	//	}

	/**
	 * 查詢 作業類別 關聯的 課程分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 作業類別 關聯的 課程分頁對象")
	public R<IPage<JobCourseVO>> findJobTypePage(@RequestParam @Schema(type = "string") Long jobTypeId,
			@RequestParam Integer page, @RequestParam Integer size, @RequestParam(required = false) String queryText) {

		Page<JobTypeCourse> pageInfo = new Page<>(page, size);
		IPage<JobCourseVO> voPage = jobCourseManager.findJobCoursePageByJobType(pageInfo, jobTypeId, queryText);
		return R.ok(voPage);
	}

	/**
	 * 新增 作業類別 x 課程 關聯
	 * 
	 * @param addTypeCategoryDTO
	 * @return
	 */
	@Operation(summary = "新增 作業類別 x 課程類別 關聯")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> addAssociation(@RequestBody @Valid AddJobCourseDTO addTypeCategoryDTO) {
		jobTypeCourseService.add(addTypeCategoryDTO);
		return R.ok();
	}

	/**
	 * 更新 作業類別 x 課程 關聯的 必要欄位
	 * 
	 * @param putTypeCategoryDTO
	 * @return
	 */
	@Operation(summary = "更新 作業類別 x 課程 關聯的 必要欄位")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateMandatory(@RequestBody @Valid PutJobCourseDTO putTypeCategoryDTO) {
		jobTypeCourseService.update(putTypeCategoryDTO);
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
		jobTypeCourseService.remove(id);
		return R.ok();
	}

}
