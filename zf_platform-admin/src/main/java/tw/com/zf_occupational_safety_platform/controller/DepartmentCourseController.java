package tw.com.zf_occupational_safety_platform.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import tw.com.zf_occupational_safety_platform.manager.DepartmentCourseManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.DepartmentCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 部門 x 公司課程表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "部門課程 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/department-course")
public class DepartmentCourseController {

	private final AuthManager authManager;
	private final DepartmentCourseManager departmentCourseManager;

	/**
	 * 臨時AddDTO, 部門x課程 關聯 <br>
	 */
	public record AddDepartmentCourse(@NotNull @Schema(description = "部門 ID", type = "string") Long departmentId,
			@NotNull @Schema(description = "企業課程 ID", type = "string") Long companyCourseId) {
	}

	/**
	 * 查詢 部門課程 分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 部門課程 列表對象")
	@SaCheckRole("company_manager")
	public R<List<DepartmentCourseVO>> findDepartmentCourseList(
			@RequestParam(required = true) @Schema(type = "string") Long departmentId,
			@RequestParam(required = false) String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		List<DepartmentCourseVO> vos = departmentCourseManager.findDepartmentCourseList(departmentId, queryText,
				sysUserVO);
		return R.ok(vos);
	}

	/**
	 * 查詢 部門課程 分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 部門課程 分頁對象")
	@SaCheckRole("company_manager")
	public R<IPage<DepartmentCourseVO>> findDepartmentCoursePage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = true) @Schema(type = "string") Long departmentId,
			@RequestParam(required = false) String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<DepartmentCourse> pageInfo = new Page<>(page, size);
		System.out.println("查詢的部門ID:" + departmentId);
		IPage<DepartmentCourseVO> departmentPage = departmentCourseManager.findDepartmentCoursePage(pageInfo,
				departmentId, queryText, sysUserVO);
		return R.ok(departmentPage);
	}

	/**
	 * 新增 部門課程
	 * 
	 * @param addDepartmentDTO
	 * @return
	 */
	@Operation(summary = "新增 部門課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping
	public R<Void> saveDepartment(@RequestBody @Valid AddDepartmentCourse addDepartmentCourse) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentCourseManager.addCourse2Department(addDepartmentCourse, sysUserVO);
		return R.ok();
	}

	/**
	 * 根據ID 刪除 部門課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 刪除 部門課程 ")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("company_manager")
	@DeleteMapping("{id}")
	public R<Void> removeDepartmentCourse(@PathVariable @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		departmentCourseManager.removeByDepartmentCourseId(id);
		return R.ok();
	}

}
