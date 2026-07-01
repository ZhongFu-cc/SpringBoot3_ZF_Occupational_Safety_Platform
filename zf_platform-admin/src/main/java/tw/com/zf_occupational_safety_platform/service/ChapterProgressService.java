package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;

/**
 * <p>
 * 章節學習進度：每個報名者 × 章節的完成狀態 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface ChapterProgressService extends IService<ChapterProgress> {

	ChapterProgress get(Long chapterProgressId);


	/**
	 * 用戶查詢本人持有的 章節學習進度
	 * 
	 * @param chapterProgressId 主鍵ID
	 * @param userId            用戶ID
	 * @return
	 */
	ChapterProgress getByOwner(Long chapterProgressId, Long userId);

	/**
	 * 透過報名ID、章節ID、用戶ID<br>
	 * 查詢 章節學習進度
	 * 
	 * @param enrollmentId 報名ID
	 * @param chapterId    章節ID
	 * @param userId       用戶ID
	 * @return
	 */
	ChapterProgress getByEnrollmentAndChapter(Long enrollmentId, Long chapterId, Long userId);

	/**
	 * 根據報名ID 查詢所有章節進度
	 * 
	 * @param enrollmentId
	 * @return
	 */
	List<ChapterProgress> findByEnrollment(Long enrollmentId);

	List<ChapterProgress> findByChapter(Collection<Long> chapterIds);

	IPage<ChapterProgress> findPageByQuery(Page<ChapterProgress> pageInfo, String queryText);

	/**
	 * 批量創建課程章節的預設進度
	 * 
	 * @param courseEnrollmentId 報名ID
	 * @param sysUserId          用戶ID
	 * @param courseChapters     課程章節列表
	 */
	void batchCreateByCourseChapter(Long courseEnrollmentId, Long sysUserId, Collection<CourseChapter> courseChapters);

	//	void update(PutCourseCategoryDTO putCourseCategoryDTO);

	void remove(Long chapterProgressId);

	/**
	 * 根據報名ID 刪除
	 * 
	 * @param courseEnrollmentId
	 */
	void removeByEnrollmentId(Long courseEnrollmentId);

	/**
	 * 根據章節IDs 刪除
	 * 
	 * @param courseChapterIds
	 */
	void removeByChapterIds(Collection<Long> courseChapterIds);

}
