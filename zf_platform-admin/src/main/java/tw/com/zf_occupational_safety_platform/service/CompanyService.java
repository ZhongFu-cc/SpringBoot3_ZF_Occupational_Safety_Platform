package tw.com.zf_occupational_safety_platform.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;

/**
 * <p>
 * 公司表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyService extends IService<Company> {

	Company get(Long companyId);
	
	List<Company> findByQuery(String queryText);

	IPage<Company> findPageByQuery(Page<Company> pageInfo, String queryText);

	Company create(AddCompanyDTO addCompanyDTO);

	void update(PutCompanyDTO putCompanyDTO);

	void remove(Long companyId);
	
}
