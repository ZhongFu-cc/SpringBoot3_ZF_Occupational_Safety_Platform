package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.system.mapper.SysMenuMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysMenu;
import tw.com.zf_occupational_safety_platform.system.service.SysMenuService;

/**
 * <p>
 * 菜單表-最底層細部權限也存在這張表,包含路由、路由組件、路由參數... 組裝動態路由返回給前端 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	@Override
	public List<SysMenu> findBySysMenus(Collection<Long> menuIds) {
		if (menuIds != null && menuIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByIds(menuIds);
	}

}
