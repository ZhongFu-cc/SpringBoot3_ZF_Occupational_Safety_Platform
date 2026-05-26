package tw.com.zf_occupational_safety_platform.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;

/**
 * <p>
 * 課程章節與自定義表單綁定結構表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseChapterService extends IService<CourseChapter> {

	CourseChapter get(Long courseChapterId);

	
	List<CourseChapterVO> findTreeByCourseId (Long courseId);
	
	CourseChapter create(AddCourseChapterDTO addCourseChapterDTO);

	void update(PutCourseChapterDTO putCourseChapterDTO);

	void remove(Long courseChapterId);

}
