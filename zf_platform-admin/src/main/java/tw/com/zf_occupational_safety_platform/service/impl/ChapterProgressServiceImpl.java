package tw.com.zf_occupational_safety_platform.service.impl;

import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterProgress;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.mapper.ChapterProgressMapper;
import tw.com.zf_occupational_safety_platform.service.ChapterProgressService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 章節學習進度：每個報名者 × 章節的完成狀態 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class ChapterProgressServiceImpl extends ServiceImpl<ChapterProgressMapper, ChapterProgress>
		implements ChapterProgressService {

	@Override
	public ChapterProgress get(Long chapterProgressId) {
		return baseMapper.selectById(chapterProgressId);
	}
	
	@Override
	public ChapterProgress getByOwner(Long chapterProgressId, Long userId) {
		return baseMapper.selectByOwner(chapterProgressId, userId);
	}

	@Override
	public ChapterProgress getByEnrollmentAndChapter(Long enrollmentId, Long chapterId, Long userId) {
		return baseMapper.selectByEnrollmentAndChapter(enrollmentId, chapterId, userId);
	}

	@Override
	public List<ChapterProgress> findByChapter(Collection<Long> chapterIds) {
		if (chapterIds == null || chapterIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByChpaterId(chapterIds);
	}

	@Override
	public List<ChapterProgress> findByEnrollment(Long enrollmentId) {
		return baseMapper.selectByEnrollmentId(enrollmentId);
	}

	@Override
	public IPage<ChapterProgress> findPageByQuery(Page<ChapterProgress> pageInfo, String queryText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void batchCreateByCourseChapter(Long courseEnrollmentId, Long sysUserId,
			Collection<CourseChapter> courseChapters) {

		if (courseChapters == null || courseChapters.isEmpty()) {
			return;
		}

		List<ChapterProgress> list = courseChapters.stream().map(chapter -> {
			ChapterProgress chapterProgress = new ChapterProgress();
			chapterProgress.setCourseEnrollmentId(courseEnrollmentId);
			chapterProgress.setCourseChapterId(chapter.getCourseChapterId());
			chapterProgress.setCourseId(chapter.getCourseId());
			chapterProgress.setSysUserId(sysUserId);
			chapterProgress.setStatus(CourseStatusEnum.NOT_STARTED);

			// 如果當前章節的類別為測驗
			if (ChapterContentTypeEnum.QUIZ.equals(chapter.getContentType())) {
				chapterProgress.setIsQuizPassed(CommonStatusEnum.NO);
			} else {
				// 如果不是測驗，直接當作測驗通過
				chapterProgress.setIsQuizPassed(CommonStatusEnum.YES);
				chapterProgress.setQuizScore(100);
			}

			return chapterProgress;

		}).toList();

		// 批量插入
		this.saveBatch(list);

	}

	@Override
	public void remove(Long chapterProgressId) {
		baseMapper.deleteById(chapterProgressId);
	}

	@Override
	public void removeByEnrollmentId(Long courseEnrollmentId) {
		baseMapper.deleteByEnrollmentId(courseEnrollmentId);
	}

	@Override
	public void removeByChapterIds(Collection<Long> courseChapterIds) {
		if (courseChapterIds == null || courseChapterIds.isEmpty()) {
			return;
		}
		baseMapper.deleteByCourseChapterId(courseChapterIds);
	}



}
