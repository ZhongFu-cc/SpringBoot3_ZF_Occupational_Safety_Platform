package tw.com.zf_occupational_safety_platform.service;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 章節觀看明細日誌 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface ChapterWatchLogService extends IService<ChapterWatchLog> {

	ChapterWatchLog get(Long chapterWatchLogId);

	IPage<ChapterWatchLog> findPageByQuery(Page<ChapterWatchLog> pageInfo, String queryText);

	/**
	 * 創建session瀏覽紀錄
	 * 
	 * @param progressId   章節進度ID
	 * @param enrollmentId 課程ID
	 * @param userId       用戶ID
	 * @param chapterId    章節ID
	 * @return
	 */
	ChapterWatchLog create(Long progressId, Long enrollmentId, Long userId, Long chapterId);

	/**
	 * 更新session的瀏覽紀錄，預計是用redis心跳包處理<br>
	 * 當心跳包消失，再來更新結束的時間並計算 觀看秒數
	 * 
	 * @param watchLogId 主鍵ID
	 */
	void update(Long watchLogId);

	void remove(Long watchLogId);

	/**
	 * 根據報名ID 刪除
	 * 
	 * @param enrollmentId
	 */
	void removeByEnrollmentId(Long enrollmentId);

}
