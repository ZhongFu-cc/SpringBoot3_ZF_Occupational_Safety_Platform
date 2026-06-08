package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseCategoryConvert;
import tw.com.zf_occupational_safety_platform.pojo.VO.TypeCategoryVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;
import tw.com.zf_occupational_safety_platform.service.CourseCategoryService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseCategoryService;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;

/**
 * 作業類別 x 課程類別 關聯 - 管理層
 */
@Component
@RequiredArgsConstructor
public class TypeCategoryManager {

	private final JobTypeService jobTypeService;
	private final CourseCategoryService courseCategoryService;
	private final JobTypeCourseCategoryService jobTypeCourseCategoryService;
	private final CourseCategoryConvert courseCategoryConvert;

	/**
	 * 根據 jobType 查詢關聯的課程類別
	 * 
	 * @param jobTypeId
	 * @param pageInfo
	 * @param queryText
	 * @return
	 */
	public IPage<TypeCategoryVO> findCatrgoryPageByJobType(Long jobTypeId, Page<CourseCategory> pageInfo,
			String queryText) {

		// 1.先拿到作業類別 x 課程類別 關聯
		List<JobTypeCourseCategory> typeCategorys = jobTypeCourseCategoryService.findByTypeId(jobTypeId);

		// 2.提取課程類別ID , 並去重
		Set<Long> courseCategoryIds = typeCategorys.stream()
				.map(JobTypeCourseCategory::getCourseCategoryId)
				.collect(Collectors.toSet());

		// 3.查詢課程類別分頁對象
		IPage<CourseCategory> categoryPage = courseCategoryService.findPageByCategoryIdAndQuery(courseCategoryIds,
				pageInfo, queryText);

		List<TypeCategoryVO> vos = categoryPage.getRecords().stream().map(category -> {
			TypeCategoryVO vo = courseCategoryConvert.entityToVO(category);
			return vo;
		}).toList();

		Page<TypeCategoryVO> voPage = new Page<>(categoryPage.getCurrent(), categoryPage.getSize(),
				categoryPage.getTotal());
		voPage.setRecords(vos);
		return voPage;

	}

}
