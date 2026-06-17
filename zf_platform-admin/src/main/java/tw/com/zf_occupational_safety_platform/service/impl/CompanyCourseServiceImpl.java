package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CompanyCourseConvert;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.mapper.CompanyCourseMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;

/**
 * <p>
 * 企業持有課程 表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class CompanyCourseServiceImpl extends ServiceImpl<CompanyCourseMapper, CompanyCourse>
		implements CompanyCourseService {

	private final CompanyCourseConvert companyCourseConvert;

	@Override
	public CompanyCourse get(Long companyCourseId) {
		return baseMapper.selectById(companyCourseId);
	}

	@Override
	public List<CompanyCourse> findByCompany(Long companyId) {
		return baseMapper.selectByCompanyId(companyId);
	}

	@Override
	public List<CompanyCourse> findByIds(Collection<Long> companyCourseIds) {
		if (companyCourseIds == null || companyCourseIds.isEmpty()) {
			Collections.emptyList();
		}
		return baseMapper.selectBatchIds(companyCourseIds);
	}

	@Override
	public IPage<CompanyCourse> findPageBycourseIds(Page<CompanyCourse> pageInfo, Collection<Long> courseIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPage<CompanyCourse> findPageBycourseIds(Page<CompanyCourse> pageInfo, Collection<Long> courseIds,
			Long companyId) {
		if (courseIds == null || courseIds.isEmpty()) {
			Page<CompanyCourse> page = new Page<>(pageInfo.getCurrent(), pageInfo.getSize());
			return page;
		}
		return baseMapper.selectByCourseIdsAndCompanyId(pageInfo, courseIds, companyId);
	}

	@Override
	public List<CompanyCourse> findByIdsAndCompany(Collection<Long> companyCourseIds, Long companyId) {
		if (companyCourseIds != null && companyCourseIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByIdsAndCompanyId(companyCourseIds, companyId);
	}

	@Override
	public void batchAdd(Long companyId, Collection<Long> courseIds) {

		if (courseIds != null && !courseIds.isEmpty()) {
			return;
		}

		List<CompanyCourse> CompanyCourseList = courseIds.stream().map(courseId -> {
			CompanyCourse companyCourse = new CompanyCourse();
			companyCourse.setCompanyId(companyId);
			companyCourse.setCourseId(courseId);
			return companyCourse;
		}).toList();

		this.saveBatch(CompanyCourseList);

	}

	@Override
	public CompanyCourse add(AddCompanyCourseDTO addCompanyCourseDTO) {
		CompanyCourse currentCompanyCourse = baseMapper.selectByCourseIdAndCompanyId(addCompanyCourseDTO.getCourseId(),
				addCompanyCourseDTO.getCompanyId());
		if (currentCompanyCourse != null) {
			throw new CourseException("企業已持有此課程");
		}

		CompanyCourse companyCourse = companyCourseConvert.addDTOToEntity(addCompanyCourseDTO);
		baseMapper.insert(companyCourse);
		return companyCourse;
	}

	@Override
	public void remove(Long companyCourseId) {
		baseMapper.deleteById(companyCourseId);
	}

}
