package tw.com.zf_occupational_safety_platform.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUserRole;

/**
 * <p>
 * 用戶與角色 - 多對多關聯表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

	/**
	 * 根據用戶Id查詢他所擁有的角色ID
	 * 
	 * @param sysUserId
	 * @return
	 */
	default List<SysUserRole> selectBySysUserId(Long sysUserId) {
		LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUserRole::getSysUserId, sysUserId);
		return this.selectList(queryWrapper);
	}

	/**
	 * 根據用戶Id，刪除他擁有的角色
	 * 
	 * @param sysUserId
	 */
	default void deleteBySysUserId(Long sysUserId) {
		LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUserRole::getSysUserId, sysUserId);
		this.delete(queryWrapper);
	}

}
