package tw.com.zf_occupational_safety_platform.service;

import java.util.List;

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
	 * 查詢該批次的臨時表資料<br>
	 * 因為 staging_sys_user_id 會直接沿用為 sys_user_id,<br>
	 * 所以轉存後、清空前查一次,即可取得整批新使用者的ID
	 *
	 * @param batchId
	 * @return
	 */
	List<StagingSysUser> findByBatchId(String batchId);

	/**
	 * 清空臨時表資料
	 *
	 * @param batchId
	 */
	void removeByBatchId(String batchId);

}
