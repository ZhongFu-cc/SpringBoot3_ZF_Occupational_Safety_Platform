package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeCourseCategoryService extends IService<JobTypeCourseCategory> {

	JobTypeCourseCategory get(Long jobTypeCourseCategoryId);
	
	/**
	 * 根據作業類別查詢關聯
	 * 
	 * @param jobTypeId
	 * @return
	 */
	List<JobTypeCourseCategory> findByTypeId(Long jobTypeId);
	
	/**
	 * 根據 多個作業類別查詢關聯
	 * 
	 * @param jobTypeIds
	 * @return
	 */
	List<JobTypeCourseCategory> findByTypeIds(Collection<Long> jobTypeIds);

	JobTypeCourseCategory add(AddTypeCategoryDTO addTypeCategoryDTO);

	void update(PutTypeCategoryDTO putTypeCategoryDTO);

	void remove(Long typeCategoryId);
	
	void removeByTypeId(Long jobTypeId);

}
