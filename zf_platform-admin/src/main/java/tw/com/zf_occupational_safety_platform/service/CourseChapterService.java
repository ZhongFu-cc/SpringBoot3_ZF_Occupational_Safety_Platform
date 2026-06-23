package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
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

	/**
	 * 根據主鍵ID,清空VideoUrl
	 * 
	 * @param courseChapterId
	 */
	void clearVideoUrl(Long courseChapterId);

	/**
	 * 課程 總測驗章節是否存在
	 */
	boolean existQuizChapter(Long courseId);
	
	/** -------------------------------------------- */

	CourseChapter get(Long courseChapterId);

	/**
	 * 獲得不是目錄型的 課程章節
	 * 
	 * @param courseId
	 * @return
	 */
	List<CourseChapter> findNonDirectoryByCourseId(Long courseId);
	
	List<CourseChapter> findNonDirectoryByCourseIds(Collection<Long> allCourseIds);

	List<CourseChapterVO> findTreeByCourseId(Long courseId);

	CourseChapter create(AddCourseChapterDTO addCourseChapterDTO);

	void update(PutCourseChapterDTO putCourseChapterDTO);

	void remove(Long courseChapterId);



}
