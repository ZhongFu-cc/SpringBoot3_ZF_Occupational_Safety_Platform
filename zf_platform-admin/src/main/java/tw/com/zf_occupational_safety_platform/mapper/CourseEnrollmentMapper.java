package tw.com.zf_occupational_safety_platform.mapper;

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

}
