package tw.com.zf_occupational_safety_platform.system.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRoleMenu;

/**
 * <p>
 * 角色與菜單 - 多對多關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

	/**
	 * 根據角色Ids，查詢持有選單及權限
	 * 
	 * @param sysRoleIds
	 * @return
	 */
	List<SysRoleMenu> findBySysRoles(Collection<Long> sysRoleIds);
	
}
