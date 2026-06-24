package tw.com.zf_occupational_safety_platform.mapper;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;

/**
 * <p>
 * 課程報名表：記錄用戶報名課程及整體完成狀態 Mapper 接口
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {

	default List<CourseEnrollment> selectBySysUserIdAndCourseId(Long sysUserId, Long courseId) {
		LambdaQueryWrapper<CourseEnrollment> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(CourseEnrollment::getSysUserId, sysUserId).eq(CourseEnrollment::getCourseId, courseId);
		return this.selectList(queryWrapper);
	}

	/**
	 * 根據查詢條件查詢 課程報名
	 * 
	 * @param pageInfo 分頁資訊
	 * @param status   目前上課的狀態
	 * @return
	 */
	default IPage<CourseEnrollment> selectByQuery(Page<CourseEnrollment> pageInfo, CourseStatusEnum status) {
		LambdaQueryWrapper<CourseEnrollment> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(status != null, CourseEnrollment::getStatus, status);
		return this.selectPage(pageInfo, queryWrapper);
	}

	/**
	 * 根據查詢條件查詢 用戶本身的 課程報名
	 * 
	 * @param pageInfo  分頁資訊
	 * @param status    目前上課的狀態
	 * @param sysUserId 用戶ID
	 * @return
	 */
	default IPage<CourseEnrollment> selectByQueryAndOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status,
			Long sysUserId) {
		LambdaQueryWrapper<CourseEnrollment> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(status != null, CourseEnrollment::getStatus, status)
				.eq(CourseEnrollment::getSysUserId, sysUserId);
		return this.selectPage(pageInfo, queryWrapper);
	}

	/**
	 * 查詢已存在 報名課程 的用戶 和 課程
	 * 
	 * @param userIds
	 * @param courseIds
	 * @return
	 */
	default List<CourseEnrollment> selectBySysUserIdsAndCourseIds(Collection<Long> userIds,
			Collection<Long> courseIds) {
		LambdaQueryWrapper<CourseEnrollment> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(CourseEnrollment::getSysUserId, userIds).in(CourseEnrollment::getCourseId, courseIds);
		return this.selectList(queryWrapper);
	};

}
