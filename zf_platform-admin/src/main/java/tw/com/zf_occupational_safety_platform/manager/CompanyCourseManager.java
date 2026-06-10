package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.controller.CompanyCourseController.AddCompanyCourse;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CompanyCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CompanyJobTypeService;
import tw.com.zf_occupational_safety_platform.service.CompanyService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

/**
 * 企業 及 企業課程 管理層
 */
@Component
@RequiredArgsConstructor
public class CompanyCourseManager {

	private final CompanyService companyService;
	private final CompanyJobTypeService companyJobTypeService;
	private final JobTypeCourseService jobTypeCourseCategoryService;
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



}
