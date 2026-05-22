package tw.com.zf_occupational_safety_platform.system.controller;

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
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.manager.PlatformAdminManager;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 用戶表 - 用戶-平台管理者 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Tag(name = "用戶-平台管理者 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/admin")
public class PlatformAdminController {

	private final AuthManager authManager;
	private final PlatformAdminManager platformAdminManager;
	private final SysUserService sysUserService;

	/** ------------------- 子級使用者管理 -------------------------
	
	/**
	 * 根據ID查詢使用者
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID查詢使用者")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@GetMapping("{id}")
	public R<SysUser> getUser(@PathVariable("id") @Schema(type = "string") Long id) {
		SysUser sysUser = sysUserService.get(id);
		return R.ok(sysUser);
	}

	@GetMapping("clild/pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢直系的clild使用者 (企業管理者)")
	@SaCheckRole("super-admin")
	public R<IPage<SysUser>> findDirectChildUser(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<SysUser> pageInfo = new Page<>(page, size);
		IPage<SysUser> userPage = platformAdminManager.findDirectChild(pageInfo, sysUserVO, queryText);
		return R.ok(userPage);
	}

	/**
	 * 新增使用者 (企業管理者)
	 * 
	 * @param addUserDTO
	 * @return
	 */
	@Operation(summary = "新增使用者 (企業管理者)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> saveUser(@RequestBody @Valid AddSysUserDTO addUserDTO) {

		SysUserVO sysUserVO = authManager.getUserInfo();
		platformAdminManager.createCompanyUser(addUserDTO, sysUserVO);
		return R.ok();
	}

	/**
	 * 更新使用者 (企業管理者)
	 * 
	 * @param user
	 * @return
	 */
	@Operation(summary = "更新使用者 (企業管理者)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateUser(@RequestBody @Valid PutSysUserDTO putSysUserDTO) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		platformAdminManager.updateCompanyUser(putSysUserDTO, sysUserVO);
		return R.ok();
	}

	/**
	 * 根據ID刪除使用者 (企業管理者)
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID刪除使用者 (企業管理者)")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeUser(@PathVariable @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		platformAdminManager.removeCompanyUser(id, sysUserVO);
		return R.ok();
	}

}
