package tw.com.zf_occupational_safety_platform.system.mapper;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;

/**
 * <p>
 * 系統通用，大檔案分片上傳，5MB以上就可處理，這邊僅記錄這個大檔案的上傳進度 和 狀況，
 * 合併後的檔案在minio，真實的分片區塊，會放在臨時資料夾，儲存資料會在redis ； Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

	/**
	 * 分頁查詢 - 根據父級ID 和 查詢條件
	 * 
	 * @param pageInfo  分頁對象
	 * @param parentId  父級ID
	 * @param queryText 查詢條件
	 * @return
	 */
	default IPage<SysUser> selectByParentIdAndQuery(IPage<SysUser> pageInfo, Long parentId, String queryText) {

		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

		queryWrapper.eq(SysUser::getParentId, parentId).and(StringUtils.isNotBlank(queryText), w -> {
			w.like(SysUser::getEmail, queryText)
					.or()
					.like(SysUser::getPhone, queryText)
					.or()
					.like(SysUser::getRealName, queryText);
		});
		IPage<SysUser> selectPage = this.selectPage(pageInfo, queryWrapper);

		return this.selectPage(pageInfo, queryWrapper);
	}

}
