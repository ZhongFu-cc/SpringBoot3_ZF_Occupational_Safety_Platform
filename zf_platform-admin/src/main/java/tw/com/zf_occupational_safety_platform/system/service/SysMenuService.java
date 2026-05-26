package tw.com.zf_occupational_safety_platform.system.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysMenu;

/**
 * <p>
 * 菜單表-最底層細部權限也存在這張表,包含路由、路由組件、路由參數... 組裝動態路由返回給前端 服务类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysMenuService extends IService<SysMenu> {

	/**
	 * 透過ids 查詢菜單/權限列表
	 * 
	 * @param sysMenuIds
	 * @return
	 */
	List<SysMenu> findBySysMenus(Collection<Long> menuIds);

}
