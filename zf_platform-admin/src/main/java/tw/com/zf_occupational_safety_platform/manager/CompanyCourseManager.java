package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.controller.CompanyCourseController.AddCompanyCourse;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CompanyCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CompanyJobTypeService;
import tw.com.zf_occupational_safety_platform.service.CompanyService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseCategoryService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

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
	private final CourseConvert courseConvert;

	/**
	 * 獲得 單一 企業課程
	 * 
	 * @param companyCourseId
	 * @param operator
	 * @return
	 */
	public CompanyCourseVO getCompanyCourseVO(Long companyCourseId, SysUserVO operator) {
		CompanyCourse companyCourse = companyCourseService.get(companyCourseId);
		if (!companyCourse.getCompanyCourseId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		Course course = courseService.get(companyCourse.getCourseId());
		CompanyCourseVO companyCourseVO = courseConvert.entityToCompanyCourseVO(course);
		companyCourseVO.setCompanyCourseId(companyCourseId);
		companyCourseVO.setCompanyId(companyCourse.getCompanyId());

		return companyCourseVO;
	}

	/**
	 * 查詢 企業課程 分頁對象
	 * 
	 * @param pageInfo
	 * @param queryText
	 * @param operator
	 * @return
	 */
	public IPage<CompanyCourseVO> findCompanyCourseVOPage(Page<CompanyCourse> pageInfo, String queryText,
			SysUserVO operator) {

		// 先模糊查詢符合的類別，並提取ID
		Map<Long, Course> courseIdMapByQuery = courseService.findCourseIdMapByQuery(queryText);
		List<Long> courseIds = courseIdMapByQuery.keySet().stream().toList();

		IPage<CompanyCourse> companyCoursePage = companyCourseService.findPageBycourseIds(pageInfo, courseIds,
				operator.getCompanyId());

		List<CompanyCourseVO> vos = companyCoursePage.getRecords().stream().map(companyCourse -> {
			Course course = courseIdMapByQuery.get(companyCourse.getCourseId());
			CompanyCourseVO vo = courseConvert.entityToCompanyCourseVO(course);
			vo.setCompanyCourseId(companyCourse.getCompanyCourseId());
			vo.setCompanyId(companyCourse.getCompanyId());
			return vo;

		}).toList();

		Page<CompanyCourseVO> voPage = new Page<>(companyCoursePage.getCurrent(), companyCoursePage.getSize(),
				companyCoursePage.getTotal());
		voPage.setRecords(vos);
		return voPage;
	}

	/**
	 * 新增課程 到 企業課程裡
	 * 
	 * @param addCompanyCourse
	 * @param operator
	 */
	public void addCourse2Company(AddCompanyCourse addCompanyCourse, SysUserVO operator) {
		AddCompanyCourseDTO addCompanyCourseDTO = new AddCompanyCourseDTO();
		addCompanyCourseDTO.setCompanyId(operator.getCompanyId());
		addCompanyCourseDTO.setCourseId(addCompanyCourse.courseId());
		companyCourseService.add(addCompanyCourseDTO);
	}

	/**
	 * 從 企業課程裡 移除 課程
	 * 
	 * @param companyCourseId
	 * @param operator
	 */
	public void removeCourseFromCompany(Long companyCourseId, SysUserVO operator) {
		CompanyCourse companyCourse = companyCourseService.get(companyCourseId);

		if (!companyCourse.getCompanyCourseId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		companyCourseService.remove(companyCourseId);

	}

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
