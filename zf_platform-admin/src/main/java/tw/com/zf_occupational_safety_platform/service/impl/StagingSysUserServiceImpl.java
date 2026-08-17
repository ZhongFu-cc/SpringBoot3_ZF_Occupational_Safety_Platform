package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.List;

import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.mapper.StagingSysUserMapper;
import tw.com.zf_occupational_safety_platform.service.StagingSysUserService;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.StagingCheckResultDTO;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 臨時表-用戶批量匯入 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-18
 */
@Service
@RequiredArgsConstructor
public class StagingSysUserServiceImpl extends ServiceImpl<StagingSysUserMapper, StagingSysUser>
		implements StagingSysUserService {
	@Override

	public StagingCheckResultDTO executeStagingValidation(String batchId) {
		return baseMapper.executeStagingValidation(batchId);
	}

	@Override
	public List<StagingSysUser> findByBatchId(String batchId) {
		return baseMapper.selectByBatchId(batchId);
	}

	@Override
	public void removeByBatchId(String batchId) {
		baseMapper.deleteByBatchId(batchId);
	}

}
