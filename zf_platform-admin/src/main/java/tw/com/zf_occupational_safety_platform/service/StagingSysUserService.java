package tw.com.zf_occupational_safety_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.StagingCheckResultDTO;

/**
 * <p>
 * 臨時表-用戶批量匯入 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-18
 */
public interface StagingSysUserService extends IService<StagingSysUser> {

	/**
	 * 校驗Account 和 email 在臨時表中
	 * 是否重複
	 * 
	 * @param batchId
	 * @return
	 */
	StagingCheckResultDTO executeStagingValidation(String batchId);

	/**
	 * 清空臨時表資料
	 * 
	 * @param batchId
	 */
	void removeByBatchId(String batchId);

}
