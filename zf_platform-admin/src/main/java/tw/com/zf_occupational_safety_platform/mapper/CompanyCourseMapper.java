package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

	default CompanyCourse selectByCompanyIdAndCourseId(Long companyId, Long courseId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCompanyId, companyId).eq(CompanyCourse::getCourseId, courseId);
		return this.selectOne(queryWrapper);
	}

	default List<CompanyCourse> selectByCompanyId(Long companyId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCompanyId, companyId);
		return this.selectList(queryWrapper);
	}

	default List<CompanyCourse> selectByIdsAndCompanyId(Collection<Long> companyCourseIds, Long companyId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCompanyId, companyId).in(CompanyCourse::getCompanyCourseId, companyCourseIds);
		return this.selectList(queryWrapper);
	}

	default List<CompanyCourse> selectByCourseIdsAndCompanyId(Collection<Long> courseIds, Long companyId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCompanyId, companyId).in(CompanyCourse::getCourseId, courseIds);
		return this.selectList(queryWrapper);
	}

	default IPage<CompanyCourse> selectByCourseIdsAndCompanyId(Page<CompanyCourse> pageInfo, Collection<Long> courseIds,
			Long companyId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCompanyId, companyId).in(CompanyCourse::getCourseId, courseIds);
		return this.selectPage(pageInfo, queryWrapper);
	}

	default CompanyCourse selectByCourseIdAndCompanyId(Long courseId, Long companyId) {
		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyCourse::getCourseId, courseId).eq(CompanyCourse::getCompanyId, companyId);
		return this.selectOne(queryWrapper);

	}

	default List<CompanyCourse> selectByCourseIds(Collection<Long> courseIds) {
		if (courseIds == null || courseIds.isEmpty()) {
			return Collections.emptyList();
		}

		LambdaQueryWrapper<CompanyCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CompanyCourse::getCourseId, courseIds);
		return this.selectList(queryWrapper);
	}

}
