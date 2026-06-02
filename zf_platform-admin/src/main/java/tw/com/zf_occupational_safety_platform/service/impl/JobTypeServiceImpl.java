package tw.com.zf_occupational_safety_platform.service.impl;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;
import tw.com.zf_occupational_safety_platform.convert.JobTypeConvert;
import tw.com.zf_occupational_safety_platform.mapper.JobTypeMapper;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 作業類別 表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class JobTypeServiceImpl extends ServiceImpl<JobTypeMapper, JobType> implements JobTypeService {

	private final JobTypeConvert jobTypeConvert;

	@Override
	public JobType get(Long jobTypeId) {
		return baseMapper.selectById(jobTypeId);
	}

	@Override
	public IPage<JobType> findPageByQuery(Page<JobType> pageInfo, String queryText) {
		return baseMapper.selectByQuery(pageInfo, queryText);
	}

	@Override
	public JobType create(AddJobTypeDTO addJobTypeDTO) {
		JobType jobType = jobTypeConvert.addDTOToEntity(addJobTypeDTO);
		baseMapper.insert(jobType);
		return jobType;
	}

	@Override
	public void update(PutJobTypeDTO putJobTypeDTO) {
		JobType jobType = jobTypeConvert.putDTOToEntity(putJobTypeDTO);
		baseMapper.updateById(jobType);
	}

	@Override
	public void remove(Long jobTypeId) {
		baseMapper.deleteById(jobTypeId);
	}

}
