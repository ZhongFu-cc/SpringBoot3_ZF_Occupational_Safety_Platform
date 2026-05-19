package tw.com.zf_occupational_safety_platform.system.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysMenu;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;

/**
 * <p>
 * 菜單表-最底層細部權限也存在這張表,包含路由、路由組件、路由參數... 組裝動態路由返回給前端 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

	/**
	 * 根據Ids查詢列表
	 * 
	 * @param ids
	 * @return
	 */
	default List<SysMenu> selectByIds(Collection<Long> ids) {
		LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(SysMenu::getSysMenuId, ids);
		return this.selectList(queryWrapper);
	}
	
}
