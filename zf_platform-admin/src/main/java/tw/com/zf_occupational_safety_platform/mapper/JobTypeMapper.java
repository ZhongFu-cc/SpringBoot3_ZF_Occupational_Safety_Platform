package tw.com.zf_occupational_safety_platform.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;

/**
 * <p>
 * 作業類別 表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeMapper extends BaseMapper<JobType> {

	default List<JobType> selectByQuery(String queryText) {
		LambdaQueryWrapper<JobType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(queryText), JobType::getName, queryText);
		return this.selectList(queryWrapper);
	}

	default IPage<JobType> selectByQuery(Page<JobType> pageInfo, String queryText) {
		LambdaQueryWrapper<JobType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(queryText), JobType::getName, queryText);
		return this.selectPage(pageInfo, queryWrapper);
	}

}
