package tw.com.zf_occupational_safety_platform.system.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;

/**
 * <p>
 * 角色表 - 透過設置角色達成較廣泛的權限管理 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

	/**
	 * 根據Ids查詢列表
	 * 
	 * @param ids
	 * @return
	 */
	default List<SysRole> selectByIds(Collection<Long> ids) {
		LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(SysRole::getSysRoleId, ids);
		return this.selectList(queryWrapper);
	}

}
