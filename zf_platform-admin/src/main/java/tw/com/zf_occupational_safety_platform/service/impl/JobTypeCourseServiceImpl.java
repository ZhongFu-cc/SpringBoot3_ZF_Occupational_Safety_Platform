package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.JobCourseConvert;
import tw.com.zf_occupational_safety_platform.exception.JobTypeCourseException;
import tw.com.zf_occupational_safety_platform.mapper.JobTypeCourseMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseService;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class JobTypeCourseServiceImpl extends ServiceImpl<JobTypeCourseMapper, JobTypeCourse>
		implements JobTypeCourseService {

	private final JobCourseConvert jobCourseConvert;

	@Override
	public JobTypeCourse get(Long jobCourseId) {
		return baseMapper.selectById(jobCourseId);
	}

	@Override
	public List<JobTypeCourse> findByTypeId(Long jobTypeId) {
		return baseMapper.selectByJobTypeId(jobTypeId);
	}

	@Override
	public List<JobTypeCourse> findByTypeIds(Collection<Long> jobTypeIds) {
		if (jobTypeIds == null || jobTypeIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByJobTypeIds(jobTypeIds);
	}

	@Override
	public IPage<JobTypeCourse> findPageByTypeIdAndCourseIds(Page<JobTypeCourse> pageInfo, Long jobTypeId,
			Collection<Long> courseIds) {
		// courseIds沒有值，返回空Page
		if (courseIds == null || courseIds.isEmpty()) {
			return new Page<JobTypeCourse>(pageInfo.getCurrent(), pageInfo.getSize());
		}
		return baseMapper.selectByTypeIdAndCourseIds(pageInfo, jobTypeId, courseIds);
	}

	@Override
	public JobTypeCourse add(AddJobCourseDTO addJobCourseDTO) {

		JobTypeCourse currentJobTypeCourse = baseMapper.selectByTypeIdAndCourseId(addJobCourseDTO.getJobTypeId(),
				addJobCourseDTO.getCourseId());
		if (currentJobTypeCourse != null) {
			throw new JobTypeCourseException("已有相同的資料");
		}

		JobTypeCourse jobTypeCourse = jobCourseConvert.addDTOToEntity(addJobCourseDTO);

		baseMapper.insert(jobTypeCourse);
		return jobTypeCourse;
	}

	@Override
	public void update(PutJobCourseDTO putJobCourseDTO) {
		JobTypeCourse jobTypeCourse = jobCourseConvert.putDTOToEntity(putJobCourseDTO);
		baseMapper.updateById(jobTypeCourse);
	}

	@Override
	public void remove(Long jobCourseId) {
		baseMapper.deleteById(jobCourseId);
	}

	@Override
	public void removeByTypeId(Long jobTypeId) {
		baseMapper.deleteByJobTypeId(jobTypeId);
	}

}
