package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.DepartmentCourseConvert;
import tw.com.zf_occupational_safety_platform.exception.JobTypeException;
import tw.com.zf_occupational_safety_platform.mapper.DepartmentCourseMapper;
import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;

/**
 * <p>
 * 部門 x 公司課程表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class DepartmentCourseServiceImpl extends ServiceImpl<DepartmentCourseMapper, DepartmentCourse>
		implements DepartmentCourseService {

	private final DepartmentCourseConvert departmentCourseConvert;

	@Override
	public List<DepartmentCourse> findByDepartmentId(Long departmentId) {
		return baseMapper.selectByDepartmentId(departmentId);
	}

	@Override
	public void assignCourse2Department(Long departmentId, Collection<Long> courseIds) {
		if (courseIds == null) {
			throw new JobTypeException("courseIds不可為null");
		}

		// 1.先查詢目前公司持有的 course
		List<DepartmentCourse> currentAssociation = this.findByDepartmentId(departmentId);

		// 2. 提取出目前已綁定的 courseIds 集合 (用 Set 加快比對速度)
		Set<Long> currentCourseIds = currentAssociation.stream()
				.map(DepartmentCourse::getCompanyCourseId)
				.collect(Collectors.toSet());

		// 將傳入的目標 id 轉為 Set 去重
		Set<Long> targetCourseIds = new HashSet<>(courseIds);

		// 3. 計算差集
		// 【需要刪除的】：舊的有，但新的沒有
		List<Long> idsToRemove = currentCourseIds.stream()
				.filter(id -> !targetCourseIds.contains(id))
				.collect(Collectors.toList());

		// 【需要新增的】：新的有，但舊的沒有
		List<Long> idsToAdd = targetCourseIds.stream()
				.filter(id -> !currentCourseIds.contains(id))
				.collect(Collectors.toList());

		// 4. 執行資料庫操作
		// 執行移除
		if (!idsToRemove.isEmpty()) {
			baseMapper.removeCourseFromDepartment(departmentId, courseIds);
		}

		// 執行新增
		if (!idsToAdd.isEmpty()) {
			List<DepartmentCourse> newAssociations = idsToAdd.stream().map(courseId -> {
				DepartmentCourse association = new DepartmentCourse();
				association.setDepartmentId(departmentId);
				association.setCompanyCourseId(courseId);
				return association;
			}).collect(Collectors.toList());

			// 使用 MyBatis-Plus 的批量新增
			this.saveBatch(newAssociations);
		}

	}

}
