package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;

/**
 * <p>
 * 企業持有課程 表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyCourseMapper extends BaseMapper<CompanyCourse> {

	default List<CompanyCourse> selectByCourseIds(Collection<Long> courseIds) {
		if (courseIds == null || courseIds.isEmpty()) {
			return Collections.emptyList();
		}

		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CompanyCourse::getCourseId, courseIds);
		return this.selectList(queryWrapper);
	}

}
