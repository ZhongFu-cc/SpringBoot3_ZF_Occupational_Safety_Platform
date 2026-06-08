package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyJobType;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeCourseCategoryMapper extends BaseMapper<JobTypeCourseCategory> {

	// 根據 作業類別 ID查詢
	default List<JobTypeCourseCategory> selectByJobTypeId(Long jobTypeId) {
		LambdaQueryWrapper<JobTypeCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourseCategory::getJobTypeId, jobTypeId);
		return this.selectList(queryWrapper);
	}

	// 根據 作業類別 Ids 查詢
	default List<JobTypeCourseCategory> selectByJobTypeIds(Collection<Long> jobTypeIds) {
		if (jobTypeIds == null || jobTypeIds.isEmpty()) {
			return Collections.emptyList();
		}
		LambdaQueryWrapper<JobTypeCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(JobTypeCourseCategory::getJobTypeId, jobTypeIds);
		return this.selectList(queryWrapper);
	}

	/**
	 * 根據雙主鍵查詢
	 * 
	 * @param jobTypeId        作業類別ID
	 * @param courseCategoryId 課程類別ID
	 * @return
	 */
	default JobTypeCourseCategory selectByTypeIdAndCategoryId(Long jobTypeId, Long courseCategoryId) {
		LambdaQueryWrapper<JobTypeCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourseCategory::getJobTypeId, jobTypeId)
				.eq(JobTypeCourseCategory::getCourseCategoryId, courseCategoryId);
		return this.selectOne(queryWrapper);
	}

	/**
	 * 根據 作業類型ID 進行刪除
	 * 
	 * @param jobTypeId
	 */
	default void deleteByJobTypeId(Long jobTypeId) {
		LambdaQueryWrapper<JobTypeCourseCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourseCategory::getJobTypeId, jobTypeId);
		this.delete(queryWrapper);

	}

}
