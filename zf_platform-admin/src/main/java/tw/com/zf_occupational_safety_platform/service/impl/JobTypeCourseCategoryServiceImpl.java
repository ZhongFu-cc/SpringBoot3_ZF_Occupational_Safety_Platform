package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.TypeCategoryConvert;
import tw.com.zf_occupational_safety_platform.exception.JobTypeCourseCategoryException;
import tw.com.zf_occupational_safety_platform.mapper.JobTypeCourseCategoryMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseCategoryService;

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
public class JobTypeCourseCategoryServiceImpl extends ServiceImpl<JobTypeCourseCategoryMapper, JobTypeCourseCategory>
		implements JobTypeCourseCategoryService {

	private final TypeCategoryConvert typeCategoryConvert;

	@Override
	public JobTypeCourseCategory get(Long jobTypeCourseCategoryId) {
		return baseMapper.selectById(jobTypeCourseCategoryId);
	}

	@Override
	public List<JobTypeCourseCategory> findByTypeId(Long jobTypeId) {
		return baseMapper.selectByJobTypeId(jobTypeId);
	}

	@Override
	public List<JobTypeCourseCategory> findByTypeIds(Collection<Long> jobTypeIds) {
		if (jobTypeIds == null || jobTypeIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByJobTypeIds(jobTypeIds);
	}

	@Override
	public JobTypeCourseCategory add(AddTypeCategoryDTO addTypeCategoryDTO) {

		JobTypeCourseCategory currentJobTypeCourseCategory = baseMapper.selectByTypeIdAndCategoryId(
				addTypeCategoryDTO.getJobTypeId(), addTypeCategoryDTO.getCourseCategoryId());
		if (currentJobTypeCourseCategory != null) {
			throw new JobTypeCourseCategoryException("已有相同的資料");
		}

		JobTypeCourseCategory jobTypeCourseCategory = typeCategoryConvert.addDTOToEntity(addTypeCategoryDTO);

		baseMapper.insert(jobTypeCourseCategory);
		return jobTypeCourseCategory;
	}

	@Override
	public void update(PutTypeCategoryDTO putTypeCategoryDTO) {
		JobTypeCourseCategory jobTypeCourseCategory = typeCategoryConvert.putDTOToEntity(putTypeCategoryDTO);
		baseMapper.updateById(jobTypeCourseCategory);
	}

	@Override
	public void remove(Long typeCategoryId) {
		baseMapper.deleteById(typeCategoryId);
	}

	@Override
	public void removeByTypeId(Long jobTypeId) {
		baseMapper.deleteByJobTypeId(jobTypeId);
	}

}
