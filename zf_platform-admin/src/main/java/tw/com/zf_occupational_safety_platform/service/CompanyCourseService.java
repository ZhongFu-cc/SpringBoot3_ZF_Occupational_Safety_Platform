package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;

/**
 * <p>
 * 企業持有課程 表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyCourseService extends IService<CompanyCourse> {

	CompanyCourse get(Long companyCourseId);
	
	List<CompanyCourse> findByCompany(Long companyId);
	
	List<CompanyCourse> findByIds(Collection<Long> companyCourseIds);

	List<CompanyCourse> findByIdsAndCompany(Collection<Long> companyCourseIds, Long companyId);

	IPage<CompanyCourse> findPageBycourseIds(Page<CompanyCourse> pageInfo, Collection<Long> courseIds);

	IPage<CompanyCourse> findPageBycourseIds(Page<CompanyCourse> pageInfo, Collection<Long> courseIds, Long companyId);

	CompanyCourse add(AddCompanyCourseDTO addCompanyCourseDTO);

	void remove(Long companyCourseId);

	void batchAdd(Long companyId, Collection<Long> courseIds);

}
