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
 * 負責企業管理者操作的 管理層
 * 
 */
@Component
@RequiredArgsConstructor
public class CompanyManager {

	// 企業管理者 為 企業員工 創建的角色權限符
	private static final String ROLE_KEY = "employee";

	private final SysUserService sysUserService;
	private final SysUserConvert sysUserConvert;
	private final SysUserRoleService sysUserRoleService;
	private final SysRoleService sysRoleService;

	// 指派 學習任務

	// 查看 部門內員工課程達成率

	// 匯出 報表。

	/**
	 * ----------------- 企業員工管理 --------------------
	 * 
	 * /**
	 * 分頁查詢 - 直接子用戶<br>
	 * 默認會添加 parentId = 當前操作用戶ID
	 * 
	 * @param pageInfo
	 * @param sysUserVO
	 * @param queryText
	 * @return
	 */
	public IPage<SysUser> findDirectChild(Page<SysUser> pageInfo, SysUserVO sysUserVO, String queryText) {
		sysUserService.findDirectChild(pageInfo, sysUserVO.getSysUserId(), queryText);
		return null;
	}

	// 匯入 員工資料-待完成

	/**
	 * 創建 企業員工用戶
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

		// 為新的企業員工添加角色
		sysUserRoleService.assignRole2User(sysUser.getSysUserId(), sysRole.getSysRoleId());
	}

	/**
	 * 更新 企業員工用戶
	 * 
	 * @param putSysUserDTO
	 * @param sysUserVO
	 */
	public void updateCompanyUser(PutSysUserDTO putSysUserDTO, SysUserVO sysUserVO) {

		if (!putSysUserDTO.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}

		// 目前僅更新基本資料，後續有其他需求再開發
		sysUserService.update(putSysUserDTO);
	}

	// 批量更新 員工資料

	/**
	 * 刪除 企業員工用戶
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 */
	public void removeCompanyUser(Long sysUserId, SysUserVO sysUserVO) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		if (!targetSysUser.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}
		// 目前僅直接刪除資料，後續有其他需求再開發
		sysUserService.remove(sysUserId);
	}

	/**
	 * 禁用/凍結 企業員工用戶
	 * 
	 * @param sysUserId
	 */
	public void disableCompanyUser(Long sysUserId, SysUserVO sysUserVO) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		if (!targetSysUser.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}

		sysUserService.updateCompanyUserStatus(sysUserId, CommonStatusEnum.NO);
	}

	/**
	 * 啟用/解凍 企業員工用戶
	 * 
	 * @param sysUserId
	 */
	public void enableCompanyUser(Long sysUserId, SysUserVO sysUserVO) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		if (!targetSysUser.getParentId().equals(sysUserVO.getSysUserId())) {
			throw new PermissionException("您無權修改此資源，該資料不屬於您的負責範圍。");
		}
		sysUserService.updateCompanyUserStatus(sysUserId, CommonStatusEnum.YES);
	}

}
