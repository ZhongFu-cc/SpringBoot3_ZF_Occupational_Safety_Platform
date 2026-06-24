package tw.com.zf_occupational_safety_platform.system.controller;

import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

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
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.manager.CompanyManager;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.UpdateUserStatus;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 用戶表 - 用戶-企業管理者 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Tag(name = "用戶-企業管理者 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/company")
public class SysCompanyController {

	private final AuthManager authManager;
	private final SysUserService sysUserService;
	private final CompanyManager companyManager;

	/**
	 * ------------------- 子級使用者管理 -------------------------
	 */

	/**
	 * 根據ID查詢使用者
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID查詢使用者")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@GetMapping("{id}")
	public R<SysUser> getUser(@PathVariable("id") @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		SysUser sysUser = companyManager.getEmployee(id, sysUserVO);
		return R.ok(sysUser);
	}

	/**
	 * 查詢child使用者 (企業員工)
	 * 
	 * @param page
	 * @param size
	 * @param queryText
	 * @return
	 */
	@GetMapping("child/pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢child使用者 (企業員工)")
	@SaCheckRole("company_manager")
	public R<IPage<SysUser>> findDirectChildUser(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<SysUser> pageInfo = new Page<>(page, size);
		IPage<SysUser> userPage = companyManager.findEmployee(pageInfo, sysUserVO, queryText);
		return R.ok(userPage);
	}

	/**
	 * 新增使用者 (企業員工)
	 * 
	 * @param addUserDTO
	 * @return
	 */
	@Operation(summary = "新增使用者 (企業員工)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping
	public R<Void> saveUser(@RequestBody @Valid AddSysUserDTO addUserDTO) {

		SysUserVO sysUserVO = authManager.getUserInfo();
		companyManager.createEmployee(addUserDTO, sysUserVO);
		return R.ok();
	}

	/**
	 * Excel 匯入 批量新增使用者 (企業員工)
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@Operation(summary = "Excel 匯入 批量新增使用者 (企業員工)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping("import-excel")
	public R<Void> importExcelUser(@RequestBody MultipartFile file) throws IOException {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyManager.importExcel(file, sysUserVO);
		return R.ok();
	}

	/**
	 * 更新使用者 (企業員工)
	 * 
	 * @param user
	 * @return
	 */
	@Operation(summary = "更新使用者 (企業員工)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PutMapping
	public R<Void> updateUser(@RequestBody @Valid PutSysUserDTO putSysUserDTO) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyManager.updateEmployee(putSysUserDTO, sysUserVO);
		return R.ok();
	}

	/**
	 * 根據ID刪除使用者 (企業員工)
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID刪除使用者 (企業員工)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("company_manager")
	@DeleteMapping("{id}")
	public R<Void> removeUser(@PathVariable @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyManager.removeEmployee(id, sysUserVO);
		return R.ok();
	}

	/**
	 * 切換使用者 (企業員工) 啟用狀態
	 * 
	 * @param updateUserStatus
	 * @return
	 */
	@Operation(summary = "切換使用者 (企業員工) 啟用狀態")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PutMapping("/status")
	public R<Void> switchStatus(@RequestBody @Valid UpdateUserStatus updateUserStatus) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyManager.switchEmployeeStatus(updateUserStatus.getSysUserId(), updateUserStatus.getStatus(), sysUserVO);
		return R.ok();
	}

}
