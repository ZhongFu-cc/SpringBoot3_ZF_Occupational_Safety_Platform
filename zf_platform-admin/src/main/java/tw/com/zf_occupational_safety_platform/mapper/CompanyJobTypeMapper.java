package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyJobType;

/**
 * <p>
 * 公司 x 作業類別 關聯表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface CompanyJobTypeMapper extends BaseMapper<CompanyJobType> {

	// 根據企業ID查詢
	default List<CompanyJobType> selectByCompanyId(Long companyId) {
		LambdaQueryWrapper<CompanyJobType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyJobType::getCompanyId, companyId);
		return this.selectList(queryWrapper);
	}

	// 從企業移除作業類型
	default void removeTypeFromCompany(Long companyId, Collection<Long> jobTypeIds) {
		if (jobTypeIds == null || jobTypeIds.isEmpty()) {
			return;
		}
		LambdaQueryWrapper<CompanyJobType> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CompanyJobType::getCompanyId, companyId).in(CompanyJobType::getJobTypeId, jobTypeIds);
		this.delete(queryWrapper);

	}

}
