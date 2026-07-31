package tw.com.zf_occupational_safety_platform.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.Company;

/**
 * <p>
 * 公司表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyMapper extends BaseMapper<Company> {

	default List<Company> selectByQuery(String queryText) {
		LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(queryText), Company::getName, queryText);
		return this.selectList(queryWrapper);
	}

	default IPage<Company> selectByQuery(Page<Company> pageInfo, String queryText) {
		LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(queryText), Company::getName, queryText);
		return this.selectPage(pageInfo, queryWrapper);
	}

}
