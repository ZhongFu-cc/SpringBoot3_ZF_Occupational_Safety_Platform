package tw.com.zf_occupational_safety_platform.mapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 章節影片 - 只有content_type 為video的才有 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-06-09
 */
public interface ChapterVideoMapper extends BaseMapper<ChapterVideo> {

	/**
	 * 根據課程章節ID,查詢Video<br>
	 * 1:1 關係
	 * 
	 * @param courseChapterId
	 * @return
	 */
	default ChapterVideo selectByCourseChapterId(Long courseChapterId) {
		LambdaQueryWrapper<ChapterVideo> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterVideo::getCourseChapterId, courseChapterId);
		return this.selectOne(queryWrapper);
	}

	/**
	 * 根據課程章節ID,進行刪除
	 * 
	 * @param courseChapterId
	 */
	default void deleteByCourseChapterId(Long courseChapterId) {
		LambdaQueryWrapper<ChapterVideo> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ChapterVideo::getCourseChapterId, courseChapterId);
		this.delete(queryWrapper);
	}
}
