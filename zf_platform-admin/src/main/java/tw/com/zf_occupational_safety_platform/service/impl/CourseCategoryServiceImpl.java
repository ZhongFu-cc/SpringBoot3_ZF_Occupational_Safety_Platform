package tw.com.zf_occupational_safety_platform.service.impl;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.convert.CourseCategoryConvert;
import tw.com.zf_occupational_safety_platform.mapper.CourseCategoryMapper;
import tw.com.zf_occupational_safety_platform.service.CourseCategoryService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 課程類別 與 法規限制表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@RequiredArgsConstructor
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
		implements CourseCategoryService {

	private final CourseCategoryConvert courseCategoryConvert;

	@Override
	public CourseCategory get(Long courseCategoryId) {
		return baseMapper.selectById(courseCategoryId);
	}

	@Override
	public IPage<CourseCategory> findPageByQuery(Page<CourseCategory> pageInfo, String queryText) {
		return baseMapper.selectByQuery(pageInfo, queryText);
	}

	@Override
	public IPage<CourseCategory> findPageByCategoryIdAndQuery(Collection<Long> categoryIds,
			Page<CourseCategory> pageInfo, String queryText) {

		if (categoryIds == null || categoryIds.isEmpty()) {
			return new Page<CourseCategory>(pageInfo.getCurrent(), pageInfo.getSize());
		}

		return baseMapper.selectByCategoryIdsAndQuery(categoryIds, pageInfo, queryText);
	}

	@Override
	public CourseCategory create(AddCourseCategoryDTO addCourseCategoryDTO) {
		CourseCategory courseCategory = courseCategoryConvert.addDTOToEntity(addCourseCategoryDTO);
		baseMapper.insert(courseCategory);
		return courseCategory;
	}

	@Override
	public void update(PutCourseCategoryDTO putCourseCategoryDTO) {
		CourseCategory courseCategory = courseCategoryConvert.putDTOToEntity(putCourseCategoryDTO);
		baseMapper.updateById(courseCategory);
	}

	@Override
	public void remove(Long courseCategoryId) {
		baseMapper.deleteById(courseCategoryId);
	}

}
