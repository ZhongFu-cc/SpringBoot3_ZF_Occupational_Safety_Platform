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
import tw.com.zf_occupational_safety_platform.manager.DepartmentManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 部門表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "部門 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/department")
public class DepartmentController {

	private final AuthManager authManager;
	private final DepartmentManager departmentManager;
	private final DepartmentService departmentService;

	/**
	 * 根據ID 查詢 部門
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 部門")
	@GetMapping("{id}")
	@SaCheckRole("company_manager")
	public R<Department> getDepartment(@PathVariable("id") @Schema(type = "string") Long id) {
		Department courseCategory = departmentService.get(id);
		return R.ok(courseCategory);
	}

	/**
	 * 查詢 部門分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 部門分頁對象")
	@SaCheckRole("company_manager")
	public R<IPage<Department>> findDepartmentPage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {

		Page<Department> pageInfo = new Page<>(page, size);
		IPage<Department> courseCategoryPage = departmentService.findPageByQuery(pageInfo, queryText);
		return R.ok(courseCategoryPage);
	}

	/**
	 * 新增 部門
	 * 
	 * @param addDepartmentDTO
	 * @return
	 */
	@Operation(summary = "新增 部門")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping
	public R<Void> saveDepartment(@RequestBody @Valid AddDepartmentDTO addDepartmentDTO) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentManager.createDepartment(addDepartmentDTO, sysUserVO);
		return R.ok();
	}

	/**
	 * 更新 部門
	 * 
	 * @param putDepartmentDTO
	 * @return
	 */
	@Operation(summary = "更新 部門")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PutMapping
	public R<Void> updateDepartment(@RequestBody @Valid PutDepartmentDTO putDepartmentDTO) {
		departmentService.update(putDepartmentDTO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 部門
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 部門 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("company_manager")
	@DeleteMapping("{id}")
	public R<Void> removeDepartment(@PathVariable @Schema(type = "string") Long id) {
		departmentService.remove(id);
		return R.ok();
	}

}
