package tw.com.zf_occupational_safety_platform.system.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.system.convert.SysMenuConvert;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.pojo.BO.RouteBO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.LoginInfo;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysMenu;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRoleMenu;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUserRole;
import tw.com.zf_occupational_safety_platform.system.service.SysMenuService;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleMenuService;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 負責登入/校驗 管理層
 * 
 */
@Component
@RequiredArgsConstructor
public class AuthManager {

	private final SysUserService sysUserService;
	private final SysUserConvert sysUserConvert;
	private final SysUserRoleService sysUserRoleService;
	private final SysRoleService sysRoleService;
	private final SysRoleMenuService sysRoleMenuService;
	private final SysMenuService sysMenuService;
	private final SysMenuConvert sysMenuConvert;

	/**
	 * 獲取使用者的資料、角色、權限
	 * 
	 * @param sysUserId
	 * @return
	 */
	public SysUserVO getSysUserVO(Long sysUserId) {

		// 獲取用戶資料
		SysUser sysUser = sysUserService.get(sysUserId);

		// 調用Service層私有方法,來獲取需要組裝的SysUserVO
		SysUserVO sysUserVO = buildUserPermissions(sysUser);

		// 返回組裝過的SysUserVO
		return sysUserVO;
	}

	/**
	 * 使用者登入，返回基本資料、角色、權限<br>
	 * 並將組裝好的sysUserInfo 放到緩存中
	 * 
	 * @param loginInfo
	 * @return
	 */
	public SysUserVO login(LoginInfo loginInfo) {

		// 登入查詢
		SysUser sysUser = sysUserService.login(loginInfo);

		// 調用Service層私有方法,來獲取需要組裝的SysUserVO
		SysUserVO sysUserVO = buildUserPermissions(sysUser);

		// 透過userId做登入
		StpUtil.login(sysUserVO.getSysUserId());

		// 登入後才能取得session
		SaSession session = StpUtil.getSession();

		// 登入後才能獲得token信息
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

		// 將登入信息進行組裝
		sysUserVO.setSaTokenInfo(tokenInfo);

		// 設定session
		session.set("userInfo", sysUserVO);

		// 返回susUserVO對象給前端
		return sysUserVO;

	}

	/**
	 * 使用者登出
	 */
	public void logout() {
		// 當前會話註銷登錄
		StpUtil.logout();

	}

	/**
	 * 獲取使用者登入的快取資料
	 * 
	 * @return
	 */
	public SysUserVO getUserInfo() {

		// 登入後才能取得session
		SaSession session = StpUtil.getSession();
		// 獲取當前使用者的資料
		SysUserVO sysUserVO = (SysUserVO) session.get("userInfo");

		return sysUserVO;
	}

	// 組裝使用者角色 及 權限的私有方法
	private SysUserVO buildUserPermissions(SysUser sysUser) {

		// VO對象,填充用戶資訊
		SysUserVO sysUserVO = sysUserConvert.entityToVO(sysUser);

		// -------------- 填充角色 ------------------------

		// 從用戶-角色表中,根據用戶Id找到他所擁有的角色ID，並提取角色ID
		List<SysUserRole> sysUserRoleList = sysUserRoleService.findBySysUser(sysUser.getSysUserId());
		List<Long> roleIds = sysUserRoleList.stream().map(SysUserRole::getSysRoleId).collect(Collectors.toList());

		// 從角色表中查詢包含角色Id列表中的所有角色資訊，並提取roleKey
		List<SysRole> sysRoleList = sysRoleService.findBySysRoles(roleIds);
		List<String> roleKeyList = sysRoleList.stream().map(SysRole::getRoleKey).collect(Collectors.toList());

		// VO對象,填充用戶所擁有的角色
		sysUserVO.setRoleList(roleKeyList);

		// ---------------- 填充權限 -----------------------

		// 從角色-菜單表中查詢包含角色ID列表中的所有菜單資訊，並提取menuId
		List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.findBySysRoles(roleIds);
		List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getSysMenuId).collect(Collectors.toList());

		// 從菜單表中查詢包含菜單ID列表中的所有菜單資訊
		List<SysMenu> sysMenuList = sysMenuService.findBySysMenus(menuIdList);

		// 獲取權限列表
		List<String> permissionList = sysMenuList.stream()
				.filter(menu -> menu.getPermission() != null && !menu.getPermission().isEmpty())
				.map(SysMenu::getPermission)
				.collect(Collectors.toList());

		// VO對象,填充用戶所擁有的權限
		sysUserVO.setPermissionList(permissionList);

		// ------------------------- 填充路由 ---------------------------------

		// 使用Menu資料組裝user的路由
		ArrayList<RouteBO> allRouteList = new ArrayList<>();

		// 遍歷Menu先取得全部的路由
		for (SysMenu sysMenu : sysMenuList) {
			// 當Menu的類型不為F , function級別時,代表他是M(Menu菜單), 或者C(Core核心頁面)
			if (!sysMenu.getMenuType().equals("F")) {

				// sysMenu轉換成BO對象, 用於組裝返回前端的路由對象
				RouteBO route = sysMenuConvert.entityToBO(sysMenu);

				// 將每個路由對象添加到List, 成為一個路由數組ArrayList<RouteBO>
				allRouteList.add(route);
			}
		}

		// 使用递归组装父子路由
		List<RouteBO> routeVO = new ArrayList<>();
		for (RouteBO route : allRouteList) {
			// 當這個路由的parentId為0 那麼他是最頂層的路由
			if (route.getParentId() == 0) {

				assembleRoutes(route, allRouteList);
				// 裝進返回給前端的路由中
				routeVO.add(route);
			}
		}

		// 將組裝後的父子路由放路sysUserVO
		sysUserVO.setRouteList(routeVO);

		return sysUserVO;
	}

	/**
	 * 遞歸function 用來重新組裝成父子路由的
	 * 
	 * @param parent 當前路由
	 * @param routes 所有路由List
	 */
	private void assembleRoutes(RouteBO parent, List<RouteBO> routes) {
		for (RouteBO route : routes) {
			if (route.getParentId() == parent.getMenuId()) {
				// 遞歸執行assembleRoutes , 確保子路由都已经被正确地组装到了当前路由对象中
				assembleRoutes(route, routes);
				parent.addChild(route);
			}
		}
	}

}
