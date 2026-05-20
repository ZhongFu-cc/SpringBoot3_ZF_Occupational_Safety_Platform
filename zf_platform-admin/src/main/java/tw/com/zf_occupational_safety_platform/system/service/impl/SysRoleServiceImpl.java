package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.system.mapper.SysRoleMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleService;

/**
 * <p>
 * 角色表 - 透過設置角色達成較廣泛的權限管理 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	@Override
	public SysRole getByRoleKey(String roleKey) {
		return baseMapper.selectByRoleKey(roleKey);
	}
	
	@Override
	public List<SysRole> findBySysRoles(Collection<Long> roleIds) {
		if (roleIds != null && roleIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByIds(roleIds);
	}



}
