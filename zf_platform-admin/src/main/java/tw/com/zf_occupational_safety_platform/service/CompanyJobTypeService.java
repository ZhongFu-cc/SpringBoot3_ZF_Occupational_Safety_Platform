package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyJobType;

/**
 * <p>
 * 公司 x 作業類別 關聯表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyJobTypeService extends IService<CompanyJobType> {

	List<CompanyJobType> findByCompanyId(Long companyId);

	void assignType2Company(Long companyId, Collection<Long> jobTypeIds);

	void removeByTypeId(Long jobTypeId);

}
