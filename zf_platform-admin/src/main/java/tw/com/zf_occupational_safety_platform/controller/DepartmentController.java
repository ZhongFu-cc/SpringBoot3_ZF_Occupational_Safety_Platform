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

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.DepartmentManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
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

	/**
	 * 臨時AddDTO, 部門x課程 關聯 <br>
	 */
	public record AddDepartmentCourse(@NotNull @Schema(description = "部門 ID", type = "string") Long departmentId,
			@NotNull @Schema(description = "企業課程 ID", type = "string") List<Long> companyCourseId) {
	}

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
		SysUserVO sysUserVO = authManager.getUserInfo();
		Department courseCategory = departmentManager.getDepartment(id, sysUserVO);
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

		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<Department> pageInfo = new Page<>(page, size);
		IPage<Department> departmentPage = departmentManager.findDepartmentPage(pageInfo, queryText, sysUserVO);
		return R.ok(departmentPage);
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
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentManager.updateDepartment(putDepartmentDTO, sysUserVO);
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
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentManager.removeDepartment(id, sysUserVO);
		return R.ok();
	}

	
	
	
	/**
	 * 分配/移除 部門課程
	 * 
	 * @param addDepartmentDTO
	 * @return
	 */
	@Operation(summary = "分配部門課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping("assign-course")
	public R<Void> assignCourse2Department(@RequestBody @Valid AddDepartmentCourse addDepartmentCourse) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentManager.assignCourse2Department(addDepartmentCourse, sysUserVO);
		return R.ok();
	}

	/**
	 * 幫所有部門 報名課程(一鍵報名)<br>
	 * 為 「未報名課程」 的 企業員工 以及 「上完課但證書過期」 的 企業員工 報名所屬部門的課程
	 * 
	 * @return
	 */
	@Operation(summary = "幫所有部門 報名課程(一鍵報名)", description = "為 「未報名課程」 的 企業員工 以及 「上完課但證書過期」 的 企業員工 報名所屬部門的課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping("one-click-enrollment")
	public R<Void> oneClickEnrollment() {
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentManager.oneClickEnrollment(sysUserVO);
		return R.ok();
	}

}
