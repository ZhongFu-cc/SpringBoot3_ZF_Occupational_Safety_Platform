package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;

/**
 * <p>
 * 作業類別 x 課程類別 關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface JobTypeCourseService extends IService<JobTypeCourse> {

	JobTypeCourse get(Long jobCourseId);

	/**
	 * 根據作業類別查詢關聯
	 * 
	 * @param jobTypeId
	 * @return
	 */
	List<JobTypeCourse> findByTypeId(Long jobTypeId);

	IPage<JobTypeCourse> findPageByTypeIdAndCourseIds(Page<JobTypeCourse> pageInfo, Long jobTypeId,
			Collection<Long> courseIds);

	/**
	 * 根據 多個作業類別查詢關聯
	 * 
	 * @param jobTypeIds
	 * @return
	 */
	List<JobTypeCourse> findByTypeIds(Collection<Long> jobTypeIds);

	JobTypeCourse add(AddJobCourseDTO addJobCourseDTO);

	void update(PutJobCourseDTO putJobCourseDTO);

	void remove(Long jobCourseId);

	void removeByTypeId(Long jobTypeId);

}
