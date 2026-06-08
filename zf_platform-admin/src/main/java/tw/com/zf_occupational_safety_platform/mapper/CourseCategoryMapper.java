package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;

/**
 * <p>
 * 課程類別 與 法規限制表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

	/**
	 * 查詢分頁對象 + 模糊查詢
	 * 
	 * @param pageInfo
	 * @param queryText
	 * @return
	 */
	default IPage<CourseCategory> selectByQuery(Page<CourseCategory> pageInfo, String queryText) {

		LambdaQueryWrapper<CourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.and(StringUtils.isNotBlank(queryText), wrap -> {
			wrap.like(CourseCategory::getCode, queryText).or().like(CourseCategory::getName, queryText);
		});

		return this.selectPage(pageInfo, queryWrapper);
	}

	/**
	 * 
	 * 查詢分頁對象 + categoryIds 內 +模糊查詢
	 * 
	 * @param categoryIds
	 * @param pageInfo
	 * @param queryText
	 * @return
	 */
	default IPage<CourseCategory> selectByCategoryIdsAndQuery(Collection<Long> categoryIds,
			Page<CourseCategory> pageInfo, String queryText) {
		LambdaQueryWrapper<CourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CourseCategory::getCourseCategoryId, categoryIds)
				.and(StringUtils.isNotBlank(queryText), wrap -> {
					wrap.like(CourseCategory::getCode, queryText).or().like(CourseCategory::getName, queryText);
				});

		return this.selectPage(pageInfo, queryWrapper);
	}

}
