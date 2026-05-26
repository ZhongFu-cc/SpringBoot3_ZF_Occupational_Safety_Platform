package tw.com.zf_occupational_safety_platform.mapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 章節學習進度：每個報名者 × 章節的完成狀態 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface ChapterProgressMapper extends BaseMapper<ChapterProgress> {

	/**
	 * 根據報名ID刪除
	 * 
	 * @param courseEnrollmentId
	 */
	default void deleteByEnrollmentId(Long courseEnrollmentId) {
		LambdaQueryWrapper<ChapterProgress> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterProgress::getCourseEnrollmentId, courseEnrollmentId);
		this.delete(queryWrapper);
	}

}
