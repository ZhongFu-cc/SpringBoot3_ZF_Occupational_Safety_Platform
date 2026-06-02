package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CompanyConvert;
import tw.com.zf_occupational_safety_platform.mapper.CompanyMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;
import tw.com.zf_occupational_safety_platform.service.CompanyService;

/**
 * <p>
 * 公司表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

	private final CompanyConvert companyConvert;

	@Override
	public Company get(Long companyId) {
		return baseMapper.selectById(companyId);
	}

	@Override
	public IPage<Company> findPageByQuery(Page<Company> pageInfo, String queryText) {
		return baseMapper.selectByQuery(pageInfo, queryText);
	}

	@Override
	public Company create(AddCompanyDTO addCompanyDTO) {
		Company company = companyConvert.addDTOToEntity(addCompanyDTO);
		baseMapper.insert(company);
		return company;
	}

	@Override
	public void update(PutCompanyDTO putCompanyDTO) {
		Company company = companyConvert.putDTOToEntity(putCompanyDTO);
		baseMapper.updateById(company);
	}

	@Override
	public void remove(Long companyId) {
		baseMapper.deleteById(companyId);
	}

}
