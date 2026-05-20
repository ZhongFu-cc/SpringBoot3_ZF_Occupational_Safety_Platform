package tw.com.zf_occupational_safety_platform.system.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.system.manager.SystemManager;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.LoginInfo;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 用戶表 - 存取系統用戶個人信息 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Tag(name = "後台用戶API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/sys-user")
public class SysUserController {

	private final SystemManager systemManager;
	private final SysUserService sysUserService;

	/**
	 * 根據ID查詢User 後台管理者
	 * 
	 * @param id
	 * @return User
	 */
	@Operation(summary = "根據ID查詢使用者資料")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@GetMapping("{id}")
	public R<SysUser> getUser(@PathVariable("id") Long id) {
		SysUser sysUser = sysUserService.get(id);
		return R.ok(sysUser);
	}

	/**
	 * 新增使用者
	 * 
	 * @param addUserDTO
	 * @return
	 */
	@Operation(summary = "新增使用者")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> saveUser(@RequestBody @Valid AddSysUserDTO addUserDTO) {
		// 待更新
		return R.ok();
	}

	/**
	 * 根據實體類User 更新後台管理者 待更新
	 * 
	 * @param user
	 * @return
	 */
	@Operation(summary = "更新後台管理者資訊")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateUser(@RequestBody @Valid PutSysUserDTO putSysUserDTO) {
		// 待更新
		return R.ok();
	}

	/**
	 * 根據ID移除User 後台管理者 如果返回的User對象為null則返回R.fail
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID刪除使用者")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("super-admin")
	@DeleteMapping("{id}")
	public R<Void> removeUser(@PathVariable Long id) {
		sysUserService.remove(id);
		return R.ok();
	}

	/**
	 * 登入時只會獲得token資訊<br>
	 * 要拿著token資訊再呼叫/system/sys-user/getUserInfo<br>
	 * 拿到更詳細的資訊
	 * 
	 * @param loginInfo
	 * @return
	 */
	@Operation(summary = "使用者登入")
	@PostMapping("login")
	public R<SaTokenInfo> login(@RequestBody @Valid LoginInfo loginInfo) {

		// 驗證登入資料
		systemManager.login(loginInfo);

		// 登入後才能獲得token信息，獲取token
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

		// 返回token
		return R.ok(tokenInfo);

	}

	/**
	 * 管理者登出方法
	 * 
	 * @return
	 */
	@SaCheckLogin
	@Operation(summary = "管理者登出")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@PostMapping("logout")
	public R<Void> logout() {
		// 驗證登入資料
		systemManager.logout();
		return R.ok();
	}

	/**
	 * 獲取緩存內的管理者資訊
	 * 
	 * @param loginInfo
	 * @return
	 */
	@Operation(summary = "獲取緩存內的管理者資訊")
	@SaCheckLogin
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@GetMapping("getUserInfo")
	public R<SysUserVO> GetUserInfo() {

		// 驗證登入資料
		SysUserVO userInfo = systemManager.getUserInfo();

		// 返回token
		return R.ok(userInfo);

	}

}
