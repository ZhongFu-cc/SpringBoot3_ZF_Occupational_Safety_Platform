package tw.com.zf_occupational_safety_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.StagingCheckResultDTO;

/**
 * <p>
 * 臨時表-用戶批量匯入 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-18
 */
public interface StagingSysUserMapper extends BaseMapper<StagingSysUser> {

	@Select("""
			SELECT account, email, dup_type AS dupType FROM (
			    -- 1. 檢查 Excel 內部 Account 是否重複
			    SELECT account, CAST(NULL AS CHAR) AS email, 'EXCEL_ACCOUNT' AS dup_type
			    FROM staging_sys_user
			    WHERE batch_id = #{batchId}
			    GROUP BY account
			    HAVING COUNT(*) > 1

			    UNION ALL

			    -- 2. 檢查 Excel 內部 Email 是否重複
			    SELECT CAST(NULL AS CHAR) AS account, email, 'EXCEL_EMAIL' AS dup_type
			    FROM staging_sys_user
			    WHERE batch_id = #{batchId}
			    GROUP BY email
			    HAVING COUNT(*) > 1

			    UNION ALL

			    -- 3. 檢查與 DB 正式表 Account 重複
			    SELECT s.account, s.email, 'DB_DUP' AS dup_type
			    FROM staging_sys_user s
			    INNER JOIN sys_user u ON u.account = s.account
			    WHERE s.batch_id = #{batchId}

			    UNION ALL

			    -- 4. 檢查與 DB 正式表 Email 重複
			    SELECT s.account, s.email, 'DB_DUP' AS dup_type
			    FROM staging_sys_user s
			    INNER JOIN sys_user u ON u.email = s.email
			    WHERE s.batch_id = #{batchId}
			) t LIMIT 1
			""")
	StagingCheckResultDTO executeStagingValidation(@Param("batchId") String batchId);

	default List<StagingSysUser> selectByBatchId(String batchId) {
		LambdaQueryWrapper<StagingSysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(StagingSysUser::getBatchId, batchId);
		return this.selectList(queryWrapper);
	}

	default void deleteByBatchId(String batchId) {
		LambdaQueryWrapper<StagingSysUser> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(StagingSysUser::getBatchId, batchId);
		this.delete(queryWrapper);
	}

}
