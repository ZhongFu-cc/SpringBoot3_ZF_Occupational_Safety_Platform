package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;

/**
 * <p>
 * 課程報名表：記錄用戶報名課程及整體完成狀態 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface CourseEnrollmentService extends IService<CourseEnrollment> {

	CourseEnrollment get(Long courseEnrollmentId);

	IPage<CourseEnrollment> findPageByQuery(Page<CourseEnrollment> pageInfo, CourseStatusEnum status);

	/**
	 * 用戶查詢自己持有課程的分頁對象
	 * 
	 * @param pageInfo 分頁資訊
	 * @param status   課程狀態
	 * @param userId   用戶ID
	 * @return
	 */
	IPage<CourseEnrollment> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status, Long userId);

	/**
	 * 報名課程，並代入基礎設定
	 * 
	 * @param sysUserId
	 * @param courseId
	 * @return
	 */
	CourseEnrollment create(Long sysUserId, Long courseId);

	/**
	 * 批量幫同一位用戶，報名多個課程，並代入基礎設定
	 * 
	 * @param sysUserId
	 * @param courseIds
	 */
	void batchCreate(Long sysUserId, Collection<Long> courseIds);

	//	void update(PutCourseCategoryDTO putCourseCategoryDTO);

	void remove(Long courseEnrollmentId);

}
