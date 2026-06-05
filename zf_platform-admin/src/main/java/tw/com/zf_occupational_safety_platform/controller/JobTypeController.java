package tw.com.zf_occupational_safety_platform.controller;

import java.util.List;

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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.JobTypeManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 作業類別 表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "作業類別 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/job-type")
public class JobTypeController {

	private final JobTypeService jobTypeService;
	private final JobTypeManager jobTypeManager;

	/**
	 * 根據ID 查詢 作業類別
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 作業類別")
	@GetMapping("{id}")
	@SaCheckLogin
	public R<JobType> getJobType(@PathVariable("id") @Schema(type = "string") Long id) {
		JobType jobType = jobTypeService.get(id);
		return R.ok(jobType);
	}

	/**
	 * 查詢 作業類別 列表對象
	 * 
	 * @param queryText
	 * @return
	 */
	@GetMapping
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 作業類別 列表對象")
	@SaCheckLogin
	public R<List<JobType>> findJobTypePage(@RequestParam(required = false) String queryText) {
		List<JobType> jobTypes = jobTypeService.list(queryText);
		return R.ok(jobTypes);
	}

	/**
	 * 查詢 作業類別分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 作業類別分頁對象")
	@SaCheckLogin
	public R<IPage<JobType>> findJobTypePage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {

		Page<JobType> pageInfo = new Page<>(page, size);
		IPage<JobType> jobTypePage = jobTypeService.findPageByQuery(pageInfo, queryText);
		return R.ok(jobTypePage);
	}

	/**
	 * 新增 作業類別
	 * 
	 * @param addJobTypeDTO
	 * @return
	 */
	@Operation(summary = "新增 作業類別")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> saveJobType(@RequestBody @Valid AddJobTypeDTO addJobTypeDTO) {
		jobTypeService.create(addJobTypeDTO);
		return R.ok();
	}

	/**
	 * 更新 作業類別
	 * 
	 * @param putJobTypeDTO
	 * @return
	 */
	@Operation(summary = "更新 作業類別")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateJobType(@RequestBody @Valid PutJobTypeDTO putJobTypeDTO) {
		jobTypeService.update(putJobTypeDTO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 作業類別
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 作業類別 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeJobType(@PathVariable @Schema(type = "string") Long id) {
		jobTypeManager.removeJobType(id);
		return R.ok();
	}

}
