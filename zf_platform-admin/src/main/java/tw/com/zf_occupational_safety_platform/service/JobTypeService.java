package tw.com.zf_occupational_safety_platform.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;

/**
 * <p>
 * 作業類別 表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeService extends IService<JobType> {

	JobType get(Long jobTypeId);

	List<JobType> list(String queryText);
	
	IPage<JobType> findPageByQuery(Page<JobType> pageInfo, String queryText);

	JobType create(AddJobTypeDTO addJobTypeDTO);

	void update(PutJobTypeDTO putJobTypeDTO);

	void remove(Long jobTypeId);
	
	
}
