package tw.com.zf_occupational_safety_platform.mapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 章節觀看明細日誌 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface ChapterWatchLogMapper extends BaseMapper<ChapterWatchLog> {

	/**
	 * 根據報名ID 刪除
	 * 
	 * @param courseEnrollmentId
	 */
	default void deleteByCourseEnrollmentId(Long courseEnrollmentId) {
		LambdaQueryWrapper<ChapterWatchLog> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterWatchLog::getCourseEnrollmentId, courseEnrollmentId);
		this.delete(queryWrapper);

	}
}
