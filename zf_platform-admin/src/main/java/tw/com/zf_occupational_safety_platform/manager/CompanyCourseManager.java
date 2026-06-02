package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CompanyJobTypeService;
import tw.com.zf_occupational_safety_platform.service.CompanyService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseCategoryService;

/**
 * 企業 及 企業課程 管理層
 */
@Component
@RequiredArgsConstructor
public class CompanyCourseManager {

	private final CompanyService companyService;
	private final CompanyJobTypeService companyJobTypeService;
	private final JobTypeCourseCategoryService jobTypeCourseCategoryService;
	private final CourseService courseService;
	private final CompanyCourseService companyCourseService;

	/**
	 * 創建企業
	 */
	public void createCompany(AddCompanyDTO addCompanyDTO) {
		// 1.創建企業
		Company company = companyService.create(addCompanyDTO);
		// 2.為企業 分配 作業類別 
		companyJobTypeService.assignType2Company(company.getCompanyId(), addCompanyDTO.getJobTypeIds());

		// 3.查詢適用 企業作業類別 x 課程類別
		List<JobTypeCourseCategory> typeCategorys = jobTypeCourseCategoryService
				.findByTypeIds(addCompanyDTO.getJobTypeIds());

		// 4.拿到去重 且 必要的 課程類別
		Set<Long> courseCategoryIds = typeCategorys.stream()
				// 1. 先過濾：只保留 isMandatory 為 YES 的資料
				.filter(tc -> CommonStatusEnum.YES.equals(tc.getIsMandatory()))
				// 2. 再轉換：只提取課程類別 ID
				.map(JobTypeCourseCategory::getCourseCategoryId)
				// 3. 收集成 Set（自動去重）
				.collect(Collectors.toSet());

		// 5.沒有必要 要上的課需要排入 企業課程表，那就可以離開了
		if (courseCategoryIds.isEmpty()) {
			return;
		}

		// 6.透過課程類別Ids，獲得必要學習的課程列表
		List<Course> courses = courseService.findByCategoryIds(courseCategoryIds);
		List<Long> courseIds = courses.stream().map(Course::getCourseId).toList();

		// 7.將這些課程放入 公司的課程庫
		companyCourseService.batchAdd(company.getCompanyId(), courseIds);

	}

}
