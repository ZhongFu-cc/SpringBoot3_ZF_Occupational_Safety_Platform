package tw.com.zf_occupational_safety_platform.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
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
	public IPage<CourseEnrollment> findPageByQuery(Page<CourseEnrollment> pageInfo, CourseStatusEnum status) {
		return baseMapper.selectByQuery(pageInfo, status);
	}

	@Override
	public IPage<CourseEnrollment> findPageByOwner(Page<CourseEnrollment> pageInfo, CourseStatusEnum status,
			Long userId) {
		return baseMapper.selectByQueryAndOwner(pageInfo, status, userId);
	}

	@Override
	public CourseEnrollment create(Long sysUserId, Long courseId) {

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
	public void batchCreate(Long sysUserId, Collection<Long> courseIds) {
		if (courseIds == null || courseIds.isEmpty()) {
			return;
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

	}

	@Override
	public void remove(Long courseEnrollmentId) {
		baseMapper.deleteById(courseEnrollmentId);
	}

}
