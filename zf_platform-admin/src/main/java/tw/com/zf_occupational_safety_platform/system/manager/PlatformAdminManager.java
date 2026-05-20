package tw.com.zf_occupational_safety_platform.system.manager;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 負責ZF平台管理員操作的 管理層
 * 
 */
@Component
@RequiredArgsConstructor
public class PlatformAdminManager {

	// 平台管理員 為 企業管理者 創建的角色權限符
	private static final String ROLE_KEY = "company_manager";

	private final SysUserService sysUserService;
	private final SysUserConvert sysUserConvert;
	private final SysUserRoleService sysUserRoleService;
	private final SysRoleService sysRoleService;

	/**
	 * 創建 企業管理者用戶
	 * 
	 * @param addUserDTO
	 */
	public void createCompanyUser(AddSysUserDTO addSysUserDTO, SysUserVO sysUserVO) {
		// 資料轉換後，添加parentId，並新增
		SysUser sysUser = sysUserConvert.addDTOToEntity(addSysUserDTO);
		sysUser.setParentId(sysUserVO.getSysUserId());
		sysUserService.save(sysUser);

		// 透過roleKey 拿到角色ID
		SysRole sysRole = sysRoleService.getByRoleKey(ROLE_KEY);

		// 為新的企業管理者添加角色
		sysUserRoleService.assignRole2User(sysUser.getSysUserId(), sysRole.getSysRoleId());
	}

	/**
	 * 更新 企業管理者用戶
	 * 
	 * @param putSysUserDTO
	 * @param sysUserVO
	 */
	public void updateCompanyUser(PutSysUserDTO putSysUserDTO, SysUserVO sysUserVO) {
		// 目前僅更新基本資料，後續有其他需求再開發
		sysUserService.update(putSysUserDTO);
	}

	/**
	 * 刪除 企業管理者用戶
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 */
	public void removeCompanyUser(Long sysUserId, SysUserVO sysUserVO) {
		// 目前僅直接刪除資料，後續有其他需求再開發
		sysUserService.remove(sysUserId);
	}

	/**
	 * 禁用/凍結 企業管理者用戶
	 * 
	 * @param sysUserId
	 */
	public void disableCompanyUser(Long sysUserId) {
		sysUserService.updateCompanyUserStatus(sysUserId, CommonStatusEnum.NO);
	}

	/**
	 * 啟用/解凍 企業管理者用戶
	 * 
	 * @param sysUserId
	 */
	public void enableCompanyUser(Long sysUserId) {
		sysUserService.updateCompanyUserStatus(sysUserId, CommonStatusEnum.YES);
	}

}
