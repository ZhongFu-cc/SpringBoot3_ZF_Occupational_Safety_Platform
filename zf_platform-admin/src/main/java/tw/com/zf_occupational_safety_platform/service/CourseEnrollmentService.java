package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseTrainingSummaryVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;

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

	/**
	 * 
	 * @param companyId
	 * @param courseId
	 * @return
	 */
	CourseTrainingSummaryVO getCompanyTrainingSummary(Long companyId, Long courseId);

	List<CourseEnrollment> findBySysUser(Long sysUserId);

	/**
	 * 查詢企業內課程的學習進度
	 * 
	 * @param companyId
	 * @param department
	 * @param courseId
	 * @return
	 */
	List<CourseEnrollment> findCompanyLearningProgress(Long companyId, Long department, Long courseId);

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
	 * 用戶查詢自己持有課程的分頁對象
	 * 
	 * @param pageInfo  分頁資訊
	 * @param status    課程狀態
	 * @param userId    用戶ID
	 * @param courseIds 課程IDs
	 * @return
	 */

	IPage<CourseEnrollment> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status, Long userId,
			Collection<Long> courseIds);

	/**
	 * 報名課程，並代入基礎設定
	 * 
	 * @param sysUserId
	 * @param courseId
	 * @return
	 */
	CourseEnrollment create(SysUserVO user, Long courseId);

	/**
	 * 批量幫同一位用戶，報名多個課程，並代入基礎設定<br>
	 * 僅給新註冊的sysUser(企業員工)使用
	 * 
	 * 
	 * @param sysUserId
	 * @param courseIds
	 * @return
	 */
	List<CourseEnrollment> batchCreate(SysUser user, Collection<Long> courseIds);

	//	void update(PutCourseCategoryDTO putCourseCategoryDTO);

	void remove(Long courseEnrollmentId);

	/**
	 * 根據 使用者 報名課程的狀況
	 * 
	 * @param userIds
	 * @param courseIds
	 * @return
	 */
	List<CourseEnrollment> findByUsersAndCourses(Collection<Long> userIds, Collection<Long> courseIds);

}
