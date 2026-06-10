package tw.com.zf_occupational_safety_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;

/**
 * <p>
 * 課程章節與自定義表單綁定結構表 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

	@Update("""
			    UPDATE course_chapter
			    SET video_url = NULL
			    WHERE course_chapter_id = #{courseChapterId}
			""")
	int clearVideoUrl(@Param("courseChapterId") Long courseChapterId);

	/**
	 * 根據課程ID查詢
	 * 
	 * @param courseId
	 * @return
	 */
	default List<CourseChapter> selectByCourseId(Long courseId) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CourseChapter::getCourseId, courseId);
		return this.selectList(queryWrapper);

	}

}
