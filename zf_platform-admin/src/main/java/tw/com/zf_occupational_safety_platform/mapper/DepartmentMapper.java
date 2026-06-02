package tw.com.zf_occupational_safety_platform.mapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.Company;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 部門表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface DepartmentMapper extends BaseMapper<Department> {

	default IPage<Department> selectByQuery(Page<Department> pageInfo, String queryText) {
		LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(queryText), Department::getName, queryText);
		return this.selectPage(pageInfo, queryWrapper);
	}

}
