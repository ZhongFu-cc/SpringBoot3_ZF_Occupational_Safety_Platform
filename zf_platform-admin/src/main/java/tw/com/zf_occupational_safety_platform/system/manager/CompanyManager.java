package tw.com.zf_occupational_safety_platform.system.manager;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.MissingRequestParameterException;
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
	 */

	/**
	 * 查詢企業員工
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 * @return
	 */
	public SysUser getEmployee(Long sysUserId, SysUserVO sysUserVO) {

		SysUser sysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (sysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!sysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		return sysUser;

	}

	/**
	 * 分頁查詢 - 公司內員工<br>
	 * 排除同級管理者
	 * 
	 * @param pageInfo
	 * @param sysUserVO
	 * @param queryText
	 * @return
	 */
	public IPage<SysUser> findEmployee(Page<SysUser> pageInfo, SysUserVO sysUserVO, String queryText) {
		return sysUserService.findByCompany(pageInfo, sysUserVO.getParentId(), sysUserVO.getCompanyId(), queryText);
	}

	// 匯入 員工資料-待完成

	/**
	 * 創建 企業員工用戶
	 * 
	 * @param addUserDTO
	 */
	public void createEmployee(AddSysUserDTO addSysUserDTO, SysUserVO sysUserVO) {

		if (addSysUserDTO.getDepartmentId() == null) {
			throw new MissingRequestParameterException("部門不可為空");
		}

		// 資料轉換後，添加parentId，並新增
		SysUser sysUser = sysUserConvert.addDTOToEntity(addSysUserDTO);
		sysUser.setParentId(sysUserVO.getSysUserId());
		// 不管前端companyId傳什麼，都以當前操作者的companyId為準
		sysUser.setCompanyId(sysUserVO.getCompanyId());

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
	public void updateEmployee(PutSysUserDTO putSysUserDTO, SysUserVO sysUserVO) {

		// 獲取當前用戶
		SysUser currentSysUser = sysUserService.get(putSysUserDTO.getSysUserId());

		// 父級ID == null , 最大權限者不給予操作
		if (currentSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 當前用戶的companyId 與 token解析下當前操作者的companyId 不一致則拋出錯誤信息
		if (!currentSysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 目前僅更新基本資料，後續有其他需求再開發
		sysUserService.update(putSysUserDTO);
	}

	/**
	 * 批量更新 員工資料
	 */

	/**
	 * 刪除 企業員工用戶
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 */
	public void removeEmployee(Long sysUserId, SysUserVO sysUserVO) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!targetSysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 移除此用戶目前擁有的角色關係
		sysUserRoleService.removeByUserId(sysUserId);

		// 目前僅直接刪除資料，後續有其他需求再開發
		sysUserService.remove(sysUserId);
	}

	/**
	 * 更改企業員工狀態<br>
	 * 啟用/禁用 用戶
	 * 
	 * @param sysUserId
	 * @param status
	 * @param operator
	 */
	public void switchEmployeeStatus(Long sysUserId, CommonStatusEnum status, SysUserVO operator) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!targetSysUser.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		sysUserService.updateCompanyUserStatus(sysUserId, status);
	}

}
