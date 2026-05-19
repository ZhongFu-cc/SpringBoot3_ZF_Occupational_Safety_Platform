package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.system.mapper.SysRoleMenuMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRoleMenu;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleMenuService;

/**
 * <p>
 * 角色與菜單 - 多對多關聯表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

	@Override
	public List<SysRoleMenu> findBySysRoles(Collection<Long> sysRoleId) {
		if(sysRoleId != null && sysRoleId.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectBySysRoleIds(sysRoleId);
	}

}
