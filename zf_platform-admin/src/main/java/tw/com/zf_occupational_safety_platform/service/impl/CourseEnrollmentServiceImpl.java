package tw.com.zf_occupational_safety_platform.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.mapper.CourseEnrollmentMapper;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;

/**
 * <p>
 * 課程報名表：記錄用戶報名課程及整體完成狀態 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Service
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment>
		implements CourseEnrollmentService {

	@Override
	public CourseEnrollment get(Long courseEnrollmentId) {
		return baseMapper.selectById(courseEnrollmentId);
	}

	@Override
	public List<CourseEnrollment> findBySysUser(Long sysUserId) {
		return baseMapper.selectBySysUserId(sysUserId);
	}

	@Override
	public IPage<CourseEnrollment> findPageByQuery(Page<CourseEnrollment> pageInfo, CourseStatusEnum status) {
		return baseMapper.selectByQuery(pageInfo, status);
	}

	@Override
	public IPage<CourseEnrollment> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status,
			Long userId) {
		return baseMapper.selectByQueryAndOwner(pageInfo, status, userId);
	}

	@Override
	public IPage<CourseEnrollment> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status,
			Long userId, Collection<Long> courseIds) {
		if (courseIds == null || courseIds.isEmpty()) {
			return new Page<>(pageInfo.getCurrent(), pageInfo.getSize());
		}
		return baseMapper.selectByQueryAndOwner(pageInfo, status, userId, courseIds);
	}

	@Override
	public List<CourseEnrollment> findByUsersAndCourses(Collection<Long> userIds, Collection<Long> courseIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Collections.emptyList();
		}

		if (courseIds == null || courseIds.isEmpty()) {
			return Collections.emptyList();
		}

		return baseMapper.selectBySysUserIdsAndCourseIds(userIds, courseIds);
	}

	@Override
	public CourseEnrollment create(Long sysUserId, Long courseId) {

		// 判斷課程是否有報名過
		List<CourseEnrollment> courseEnrollments = baseMapper.selectBySysUserIdAndCourseId(sysUserId, courseId);
		// 如果要報名的課程有資料並處於 未開始、進行中、已完成 任何一種狀態時，拋出錯誤
		long count = courseEnrollments.stream()
				.filter(e -> Set
						.of(CourseStatusEnum.NOT_STARTED, CourseStatusEnum.IN_PROGRESS, CourseStatusEnum.COMPLETED)
						.contains(e.getStatus()))
				.count();

		if (count > 1) {
			throw new CourseException("已有報名此課程，無法重複報名");
		}

		LocalDateTime now = LocalDateTime.now();
		CourseEnrollment courseEnrollment = new CourseEnrollment();
		courseEnrollment.setSysUserId(sysUserId);
		courseEnrollment.setCourseId(courseId);
		courseEnrollment.setCompletedChapters(0);
		courseEnrollment.setStatus(CourseStatusEnum.NOT_STARTED);
		courseEnrollment.setIsChaptersDone(CommonStatusEnum.NO);
		courseEnrollment.setIsMinutesMet(CommonStatusEnum.NO);
		courseEnrollment.setEnrolledAt(now);

		baseMapper.insert(courseEnrollment);

		return courseEnrollment;
	}

	@Override
	public List<CourseEnrollment> batchCreate(Long sysUserId, Collection<Long> courseIds) {
		if (courseIds == null || courseIds.isEmpty()) {
			return Collections.emptyList();
		}
		LocalDateTime now = LocalDateTime.now();

		List<CourseEnrollment> courseEnrollments = courseIds.stream().map(courseId -> {
			CourseEnrollment courseEnrollment = new CourseEnrollment();
			courseEnrollment.setSysUserId(sysUserId);
			courseEnrollment.setCourseId(courseId);
			courseEnrollment.setCompletedChapters(0);
			courseEnrollment.setStatus(CourseStatusEnum.NOT_STARTED);
			courseEnrollment.setIsChaptersDone(CommonStatusEnum.NO);
			courseEnrollment.setIsMinutesMet(CommonStatusEnum.NO);
			courseEnrollment.setEnrolledAt(now);
			return courseEnrollment;
		}).toList();

		this.saveBatch(courseEnrollments);

		return courseEnrollments;

	}

	@Override
	public void remove(Long courseEnrollmentId) {
		baseMapper.deleteById(courseEnrollmentId);
	}

}
