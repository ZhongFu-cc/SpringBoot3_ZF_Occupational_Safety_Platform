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

	default long countByDepartmentId(Long departmentId) {
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getDepartmentId, departmentId);
		return this.selectCount(queryWrapper);
	}

	/**
	 * 根據主鍵ID 和 公司ID 查詢
	 * 
	 * @param id
	 * @param companyId
	 * @return
	 */
	default SysUser selectByIdAndCompanyId(Long id, Long companyId) {
		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysUser::getSysUserId, id).eq(SysUser::getCompanyId, companyId);
		return this.selectOne(queryWrapper);
	}

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

	/**
	 * 分頁查詢 - 根據父級ID 和 查詢條件<br>
	 * 並排除公司ID
	 * 
	 * @param pageInfo  分頁對象
	 * @param parentId  父級ID
	 * @param companyId 公司ID
	 * @param queryText 查詢條件
	 * @return
	 */
	default IPage<SysUser> selectByParentIdAndQueryExcludeCompanyId(IPage<SysUser> pageInfo, Long parentId,
			Long companyId, String queryText) {

		LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

		// 避開所有 , 平台管理員創建 的 企業管理者
		queryWrapper.ne(SysUser::getParentId, parentId)
				// 查詢所有跟當前操作者(企業管理者) 同公司的數據
				.eq(SysUser::getCompanyId, companyId)
				// 加上查詢條件
				.and(StringUtils.isNotBlank(queryText), w -> {
					w.like(SysUser::getEmail, queryText)
							.or()
							.like(SysUser::getPhone, queryText)
							.or()
							.like(SysUser::getRealName, queryText);
				});

		return this.selectPage(pageInfo, queryWrapper);
	}

}
