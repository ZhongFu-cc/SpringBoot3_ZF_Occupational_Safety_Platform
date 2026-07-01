package tw.com.zf_occupational_safety_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.CheckFileVO;

/**
 * <p>
 * 章節影片 - 只有content_type 為video的才有 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-09
 */
public interface ChapterVideoService extends IService<ChapterVideo> {

	ChapterVideo get(Long chapterVideoId);

	/**
	 * 根據 chapterId 查詢<br>
	 * 1:1 關係
	 * 
	 * @param courseChapterId
	 * @return
	 */
	ChapterVideo getByChapter(Long courseChapterId);

	ChapterVideo create(AddChapterVideoDTO addChapterVideoDTO);

	void update(PutChapterVideoDTO putChapterVideoDTO);

	void remove(Long chapterVideoId);
	
	void removeByChapter(Long courseChapterId);

}
