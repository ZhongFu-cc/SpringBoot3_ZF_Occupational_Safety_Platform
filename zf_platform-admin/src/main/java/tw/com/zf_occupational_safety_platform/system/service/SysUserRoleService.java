package tw.com.zf_occupational_safety_platform.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUserRole;

/**
 * <p>
 * 用戶與角色 - 多對多關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysUserRoleService extends IService<SysUserRole> {

	/**
	 * 根據使用者ID查詢持有角色
	 * 
	 * @param sysUserId
	 * @return
	 */
	List<SysUserRole> findBySysUser(Long sysUserId);

	/**
	 * 為使用者 添加 角色
	 * 
	 * @param sysUserId
	 * @param sysRoleId
	 */
	void assignRole2User(Long sysUserId, Long sysRoleId);

	/**
	 * 刪除使用者所有角色<br>
	 * 用於使用者被刪除的情況
	 * 
	 * @param sysUserId
	 */
	void removeByUserId(Long sysUserId);

}
