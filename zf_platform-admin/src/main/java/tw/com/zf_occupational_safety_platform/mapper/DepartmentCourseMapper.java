package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;

/**
 * <p>
 * 部門 x 公司課程表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface DepartmentCourseMapper extends BaseMapper<DepartmentCourse> {

	default DepartmentCourse selectByDepartmentIdAndCompanyCourseId(Long departmentId, Long companyCourseId) {
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DepartmentCourse::getDepartmentId, departmentId)
				.eq(DepartmentCourse::getCompanyCourseId, companyCourseId);
		return this.selectOne(queryWrapper);
	}

	// 根據部門ID查詢
	default List<DepartmentCourse> selectByDepartmentId(Long departmentId) {
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DepartmentCourse::getDepartmentId, departmentId);
		return this.selectList(queryWrapper);
	}

	default List<DepartmentCourse> selectByDepartmentIds(Collection<Long> departmentIds) {
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DepartmentCourse::getDepartmentId, departmentIds);
		return this.selectList(queryWrapper);
	}

	default IPage<DepartmentCourse> selectByCompanyCourseIds(Page<DepartmentCourse> pageInfo,
			Collection<Long> companyCourseIds) {
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(DepartmentCourse::getCompanyCourseId, companyCourseIds);
		return this.selectPage(pageInfo, queryWrapper);
	}

	// 從部門移除課程類型
	default void removeCourseFromDepartment(Long departmentId, Collection<Long> companyCourseId) {
		if (companyCourseId == null || companyCourseId.isEmpty()) {
			return;
		}
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DepartmentCourse::getDepartmentId, departmentId)
				.in(DepartmentCourse::getCompanyCourseId, companyCourseId);
		this.delete(queryWrapper);

	}

	/**
	 * 根據部門ID移除
	 * 
	 * @param departmentId
	 */
	default void removeByDepartmentId(Long departmentId) {
		LambdaQueryWrapper<DepartmentCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DepartmentCourse::getDepartmentId, departmentId);
		this.delete(queryWrapper);
	}

}
