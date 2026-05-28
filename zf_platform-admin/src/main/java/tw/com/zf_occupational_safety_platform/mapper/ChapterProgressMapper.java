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
	 * 查詢本人持有的 章節學習進度
	 * 
	 * @param chapterProgressId 主鍵ID
	 * @param sysUserId         用戶ID
	 * @return
	 */
	default ChapterProgress selectByOwner(Long chapterProgressId, Long sysUserId) {
		LambdaQueryWrapper<ChapterProgress> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterProgress::getChapterProgressId, chapterProgressId)
				.eq(ChapterProgress::getSysUserId, sysUserId);

		return this.selectOne(queryWrapper);

	}

	/**
	 * 根據條件查詢 章節進度
	 * 
	 * @param courseEnrollmentId 報名ID
	 * @param courseChapterId    課程章節ID
	 * @param sysUserId          用戶id
	 * @return
	 */
	default ChapterProgress selectByEnrollmentAndChapter(Long courseEnrollmentId, Long courseChapterId,
			Long sysUserId) {
		LambdaQueryWrapper<ChapterProgress> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterProgress::getCourseEnrollmentId, courseEnrollmentId)
				.eq(ChapterProgress::getCourseChapterId, courseChapterId)
				.eq(ChapterProgress::getSysUserId, sysUserId);
		return this.selectOne(queryWrapper);
	};

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
