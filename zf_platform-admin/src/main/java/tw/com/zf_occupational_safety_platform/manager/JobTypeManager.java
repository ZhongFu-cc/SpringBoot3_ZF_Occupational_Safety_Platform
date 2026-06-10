package tw.com.zf_occupational_safety_platform.manager;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.service.CompanyJobTypeService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;

/**
 * 作業類別 管理層
 */
@Component
@RequiredArgsConstructor
public class JobTypeManager {

	private final CompanyJobTypeService companyJobTypeService;
	private final JobTypeService jobTypeService;
	private final JobTypeCourseService jobTypeCourseService;

	public void removeJobType(Long jobTypeId) {

		// 移除 企業 x 作業類別 的關聯
		companyJobTypeService.removeByTypeId(jobTypeId);

		// 移除 作業類別 x 課程的關聯
		jobTypeCourseService.removeByTypeId(jobTypeId);

		// 移除 作業類別
		jobTypeService.remove(jobTypeId);

	}

}
