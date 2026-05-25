package tw.com.zf_occupational_safety_platform.system.manager;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
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
 * 負責ZF平台管理員 用戶層面 管理層
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

	/** --------------------- 平台課程類別管理 ------------------------ */

	/** --------------------- 平台課程管理 ------------------------ */

	/** --------------------- 平台用戶管理 ------------------------ */

	/**
	 * 分頁查詢 - 直接子用戶<br>
	 * 默認會添加 parentId = 當前操作用戶ID
	 * 
	 * @param pageInfo
	 * @param sysUserVO
	 * @param queryText
	 * @return
	 */
	public IPage<SysUser> findDirectChild(Page<SysUser> pageInfo, SysUserVO sysUserVO, String queryText) {
		return sysUserService.findDirectChild(pageInfo, sysUserVO.getSysUserId(), queryText);
	}

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

		// 獲取當前用戶
		SysUser currentSysUser = sysUserService.get(putSysUserDTO.getSysUserId());

		// 父級ID == null , 最大權限者不給予操作
		if (currentSysUser.getParentId() == null) {
			throw new PermissionException("您無權查詢此資源，該資料不屬於您的負責範圍。");
		}

		// 當前用戶的父級ID 與 token解析下當前操作者的ID 不一致則拋出錯誤信息
		if (!currentSysUser.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}

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
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權查詢此資源，該資料不屬於您的負責範圍。");
		}
		
		if (!targetSysUser.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}

		// 移除此用戶目前擁有的角色關係
		sysUserRoleService.removeByUserId(sysUserId);

		// 目前僅直接刪除資料，後續有其他需求再開發
		sysUserService.remove(sysUserId);
	}

	/**
	 * 更改用戶狀態<br>
	 * 啟用/禁用 用戶
	 * 
	 * @param sysUserId
	 * @param status
	 * @param operator
	 */
	public void switchCompanyUserStatus(Long sysUserId, CommonStatusEnum status, SysUserVO operator) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權查詢此資源，該資料不屬於您的負責範圍。");
		}
		
		if (!targetSysUser.getParentId().equals(operator.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}

		sysUserService.updateCompanyUserStatus(sysUserId, status);
	}

}
