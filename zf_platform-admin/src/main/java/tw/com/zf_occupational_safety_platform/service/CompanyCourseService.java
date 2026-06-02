package tw.com.zf_occupational_safety_platform.service;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;

import java.util.Collection;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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

	IPage<CompanyCourse> findPageBycourseIds(Page<CompanyCourse> pageInfo,  Collection<Long> courseIds);

	CompanyCourse add(AddCompanyCourseDTO addCompanyCourseDTO);

	void remove(Long companyCourseId);
	
}
