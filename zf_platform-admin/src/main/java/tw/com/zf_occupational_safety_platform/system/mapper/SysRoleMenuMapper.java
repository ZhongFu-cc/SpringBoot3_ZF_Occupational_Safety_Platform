package tw.com.zf_occupational_safety_platform.system.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRoleMenu;

/**
 * <p>
 * 角色與菜單 - 多對多關聯表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

	/**
	 * 根據角色Ids，查詢他所擁有的選單及權限
	 * 
	 * @param sysRoleId
	 * @return
	 */
	default List<SysRoleMenu> selectBySysRoleIds(Collection<Long> sysRoleIds) {
		LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(SysRoleMenu::getSysRoleId, sysRoleIds);
		return this.selectList(queryWrapper);
	}

}
