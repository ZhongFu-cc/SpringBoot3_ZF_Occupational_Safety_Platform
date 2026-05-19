package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.system.convert.SysMenuConvert;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.mapper.SysMenuMapper;
import tw.com.zf_occupational_safety_platform.system.mapper.SysRoleMapper;
import tw.com.zf_occupational_safety_platform.system.mapper.SysRoleMenuMapper;
import tw.com.zf_occupational_safety_platform.system.mapper.SysUserMapper;
import tw.com.zf_occupational_safety_platform.system.mapper.SysUserRoleMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.BO.RouteBO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.LoginInfo;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysMenu;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRoleMenu;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUserRole;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * <p>
 * 用戶表 - 存取系統用戶個人信息 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */

@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	private final SysUserConvert sysUserConvert;
	private final SysMenuConvert sysMenuConvert;
	private final SysUserMapper baseMapper;
	private final SysUserRoleMapper sysUserRoleMapper;
	private final SysRoleMapper sysRoleMapper;
	private final SysRoleMenuMapper sysRoleMenuMapper;
	private final SysMenuMapper sysMenuMapper;

	@Override
	public SysUserVO selectSysUser(Long id) {

		// 獲取用戶資料
		SysUser sysUser = baseMapper.selectById(id);

		// 調用Service層私有方法,來獲取需要組裝的SysUserVO
		SysUserVO sysUserVO = buildUserPermissions(sysUser);

		// 返回組裝過的SysUserVO
		return sysUserVO;
	}

	@Override
	public List<SysUserVO> selectAllSysUser() {
		// 創建空List,用於蒐集SysUserVO
		List<SysUserVO> sysUserVOList = new ArrayList<>();
		// 獲取所有SysUser用戶資料
		List<SysUser> sysUserList = baseMapper.selectList(null);

		// 使用增強for循環遍歷
		for (SysUser sysUser : sysUserList) {

			// 調用Service層私有方法,來獲取需要組裝的SysUserVO
			SysUserVO sysUserVO = buildUserPermissions(sysUser);

			// 組裝完畢放入List中
			sysUserVOList.add(sysUserVO);
		}

		// 返回List<SysUserVO>
		return sysUserVOList;
	}

	@Override
	public Void insertSysUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void updateSysUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void deleteSysUser(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SysUserVO login(LoginInfo loginInfo) {

		// 判斷帳號密碼
		LambdaQueryWrapper<SysUser> loginQueryWrapper = new LambdaQueryWrapper<>();
		loginQueryWrapper.eq(SysUser::getAccount, loginInfo.getAccount()).eq(SysUser::getPassword, loginInfo.getPassword());

		// 獲取用戶資料
		SysUser sysUser = baseMapper.selectOne(loginQueryWrapper);

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

	@Override
	public void logout() {
		// TODO Auto-generated method stub

		// 当前会话注销登录
		StpUtil.logout();

	}

	
	@Override
	public SysUserVO getUserInfo() {

		// 登入後才能取得session
		SaSession session = StpUtil.getSession();
		// 獲取當前使用者的資料
		SysUserVO sysUserVO = (SysUserVO) session.get("userInfo");

		return sysUserVO;
	}

}
