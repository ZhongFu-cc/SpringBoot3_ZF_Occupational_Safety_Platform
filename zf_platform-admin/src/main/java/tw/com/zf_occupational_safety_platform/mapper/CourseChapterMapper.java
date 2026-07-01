package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
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

	/**
	 * 查詢課程內 總測驗章節的數量
	 * 
	 * @param courseId
	 * @return
	 */
	default long countByQuizChapter(Long courseId) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CourseChapter::getCourseId, courseId)
				.eq(CourseChapter::getContentType, ChapterContentTypeEnum.QUIZ);
		return this.selectCount(queryWrapper);
	}

	@Update("""
			    UPDATE course_chapter
			    SET video_url = NULL
			    WHERE course_chapter_id = #{courseChapterId}
			""")
	int clearVideoUrl(@Param("courseChapterId") Long courseChapterId);

	/**
	 * 查詢主鍵對象 , 及其child節點的對象
	 * 
	 * @param courseChapterId
	 * @return
	 */
	default List<CourseChapter> selectAllNode(Long courseChapterId) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CourseChapter::getCourseChapterId, courseChapterId)
				.in(CourseChapter::getParentId, courseChapterId);
		return this.selectList(queryWrapper);
	}

	/**
	 * 根據主鍵IDs查詢，並以ChapterOrder排序
	 * 
	 * @param courseChapterIds
	 * @return
	 */
	default List<CourseChapter> selectByIds(Collection<Long> courseChapterIds) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CourseChapter::getCourseChapterId, courseChapterIds)
				.orderByAsc(CourseChapter::getChapterOrder)
				.orderByAsc(CourseChapter::getCourseChapterId);

		return this.selectList(queryWrapper);
	}

	/**
	 * 根據課程ID查詢，並以ChapterOrder排序
	 * 
	 * @param courseId
	 * @return
	 */
	default List<CourseChapter> selectByCourseId(Long courseId) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CourseChapter::getCourseId, courseId)
				.orderByAsc(CourseChapter::getChapterOrder)
				.orderByAsc(CourseChapter::getCourseChapterId);
		return this.selectList(queryWrapper);

	}

	/**
	 * 根據課程IDs查詢
	 * 
	 * @param courseIds
	 * @return
	 */
	default List<CourseChapter> selectByCourseId(Collection<Long> courseIds) {
		LambdaQueryWrapper<CourseChapter> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CourseChapter::getCourseId, courseIds);
		return this.selectList(queryWrapper);

	}

}
