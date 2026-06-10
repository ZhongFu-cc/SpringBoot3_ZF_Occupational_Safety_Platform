package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;

/**
 * <p>
 * 作業類別 x 課程 關聯表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeCourseMapper extends BaseMapper<JobTypeCourse> {

	// 根據 作業類別 ID查詢
	default List<JobTypeCourse> selectByJobTypeId(Long jobTypeId) {
		LambdaQueryWrapper<JobTypeCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourse::getJobTypeId, jobTypeId);
		return this.selectList(queryWrapper);
	}

	// 根據 作業類別 Ids 查詢
	default List<JobTypeCourse> selectByJobTypeIds(Collection<Long> jobTypeIds) {
		if (jobTypeIds == null || jobTypeIds.isEmpty()) {
			return Collections.emptyList();
		}
		LambdaQueryWrapper<JobTypeCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(JobTypeCourse::getJobTypeId, jobTypeIds);
		return this.selectList(queryWrapper);
	}

	/**
	 * 根據雙主鍵查詢
	 * 
	 * @param jobTypeId 作業類別ID
	 * @param courseId  課程ID
	 * @return
	 */
	default JobTypeCourse selectByTypeIdAndCourseId(Long jobTypeId, Long courseId) {
		LambdaQueryWrapper<JobTypeCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourse::getJobTypeId, jobTypeId).eq(JobTypeCourse::getCourseId, courseId);
		return this.selectOne(queryWrapper);
	}

	default IPage<JobTypeCourse> selectByTypeIdAndCourseIds(Page<JobTypeCourse> pageInfo, Long jobTypeId,
			Collection<Long> courseIds) {
		LambdaQueryWrapper<JobTypeCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourse::getJobTypeId, jobTypeId).in(JobTypeCourse::getCourseId, courseIds);
		return this.selectPage(pageInfo, queryWrapper);
	}

	/**
	 * 根據 作業類型ID 進行刪除
	 * 
	 * @param jobTypeId
	 */
	default void deleteByJobTypeId(Long jobTypeId) {
		LambdaQueryWrapper<JobTypeCourse> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(JobTypeCourse::getJobTypeId, jobTypeId);
		this.delete(queryWrapper);

	}

}
