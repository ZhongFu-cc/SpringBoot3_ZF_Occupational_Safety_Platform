package tw.com.zf_occupational_safety_platform.system.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;

/**
 * <p>
 * 角色表 - 透過設置角色達成較廣泛的權限管理 服务类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysRoleService extends IService<SysRole> {

	/**
	 * 透過ids 查詢角色列表
	 * 
	 * @param roleIds
	 * @return
	 */
	List<SysRole> findBySysRoles(Collection<Long> roleIds);

}
