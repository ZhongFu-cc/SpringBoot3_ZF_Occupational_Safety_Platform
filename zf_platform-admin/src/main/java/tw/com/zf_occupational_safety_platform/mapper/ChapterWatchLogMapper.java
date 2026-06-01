package tw.com.zf_occupational_safety_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;

/**
 * <p>
 * 章節觀看明細日誌 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface ChapterWatchLogMapper extends BaseMapper<ChapterWatchLog> {

	@Select("""
			SELECT SUM(COALESCE(duration_sec, 0)) AS totalDurationSec
			FROM chapter_watch_log
			WHERE course_enrollment_id = #{courseEnrollmentId}
			GROUP BY course_enrollment_id
			""")
	Integer selectTotalDurationSec(Long courseEnrollmentId);

	/**
	 * 根據報名ID 查詢
	 * 
	 * @param courseEnrollmentId
	 */
	default List<ChapterWatchLog> selectByCourseEnrollmentId(Long courseEnrollmentId) {
		LambdaQueryWrapper<ChapterWatchLog> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterWatchLog::getCourseEnrollmentId, courseEnrollmentId);
		return this.selectList(queryWrapper);

	}

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
