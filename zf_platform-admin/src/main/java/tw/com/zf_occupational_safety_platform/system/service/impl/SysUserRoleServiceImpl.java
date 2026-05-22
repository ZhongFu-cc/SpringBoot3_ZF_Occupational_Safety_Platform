package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.system.mapper.SysUserRoleMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUserRole;
import tw.com.zf_occupational_safety_platform.system.service.SysUserRoleService;

/**
 * <p>
 * 用戶與角色 - 多對多關聯表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

	@Override
	public List<SysUserRole> findBySysUser(Long sysUserId) {
		return baseMapper.selectBySysUserId(sysUserId);
	}

	@Override
	public void assignRole2User(Long sysUserId, Long sysRoleId) {
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setSysUserId(sysUserId);
		sysUserRole.setSysRoleId(sysRoleId);
		baseMapper.insert(sysUserRole);
	}

	@Override
	public void removeByUserId(Long sysUserId) {
		baseMapper.deleteBySysUserId(sysUserId);
	}

}
