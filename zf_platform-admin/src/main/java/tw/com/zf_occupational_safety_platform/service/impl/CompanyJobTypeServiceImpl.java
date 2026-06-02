package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.exception.JobTypeException;
import tw.com.zf_occupational_safety_platform.mapper.CompanyJobTypeMapper;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyJobType;
import tw.com.zf_occupational_safety_platform.service.CompanyJobTypeService;

/**
 * <p>
 * 公司 x 作業類別 關聯表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class CompanyJobTypeServiceImpl extends ServiceImpl<CompanyJobTypeMapper, CompanyJobType>
		implements CompanyJobTypeService {

	@Override
	public List<CompanyJobType> findByCompanyId(Long companyId) {
		return baseMapper.selectByCompanyId(companyId);
	}

	@Override
	public void assignType2Company(Long companyId, Collection<Long> jobTypeIds) {
		if (jobTypeIds == null) {
			throw new JobTypeException("jobTypeIds不可為null");
		}

		// 1.先查詢目前公司持有的 jobTypes
		List<CompanyJobType> currentAssociation = this.findByCompanyId(companyId);

		// 2. 提取出目前已綁定的 jobTypeId 集合 (用 Set 加快比對速度)
		Set<Long> currentTypeIds = currentAssociation.stream()
				.map(CompanyJobType::getJobTypeId)
				.collect(Collectors.toSet());

		// 將傳入的目標 id 轉為 Set 去重
		Set<Long> targetTypeIds = new HashSet<>(jobTypeIds);

		// 3. 計算差集
		// 【需要刪除的】：舊的有，但新的沒有
		List<Long> idsToRemove = currentTypeIds.stream()
				.filter(id -> !targetTypeIds.contains(id))
				.collect(Collectors.toList());

		// 【需要新增的】：新的有，但舊的沒有
		List<Long> idsToAdd = targetTypeIds.stream()
				.filter(id -> !currentTypeIds.contains(id))
				.collect(Collectors.toList());

		// 4. 執行資料庫操作
		// 執行移除
		if (!idsToRemove.isEmpty()) {
			baseMapper.removeTypeFromCompany(companyId, jobTypeIds);
		}

		// 執行新增
		if (!idsToAdd.isEmpty()) {
			List<CompanyJobType> newAssociations = idsToAdd.stream().map(jobTypeId -> {
				CompanyJobType association = new CompanyJobType();
				association.setCompanyId(companyId);
				association.setJobTypeId(jobTypeId);
				return association;
			}).collect(Collectors.toList());

			// 使用 MyBatis-Plus 的批量新增
			this.saveBatch(newAssociations);
		}
	}

}
