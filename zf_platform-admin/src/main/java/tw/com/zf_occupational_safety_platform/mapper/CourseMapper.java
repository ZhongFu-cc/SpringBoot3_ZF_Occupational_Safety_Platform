package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.Course;

/**
 * <p>
 * 課程主表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseMapper extends BaseMapper<Course> {

	default List<Course> selectByCourseCategoryIds(Collection<Long> courseCategoryIds) {
		if (courseCategoryIds != null && courseCategoryIds.isEmpty()) {
			return Collections.emptyList();
		}
		LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(Course::getCourseCategoryId, courseCategoryIds);

		return this.selectList(queryWrapper);

	}

	/**
	 * 根據條件進行分頁查詢
	 * 
	 * @param pageInfo         分頁對象
	 * @param courseCategoryId 課程類別ID
	 * @param queryText        輸入查詢
	 * @return
	 */
	default IPage<Course> selectByQuery(Page<Course> pageInfo, Long courseCategoryId, String queryText) {

		LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(courseCategoryId != null, Course::getCourseCategoryId, courseCategoryId).and(wrap -> {
			wrap.like(Course::getTitle, queryText);
		});

		return this.selectPage(pageInfo, queryWrapper);
	}

}
