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
	public IPage<ChapterProgress> findPageByQuery(Page<ChapterProgress> pageInfo, String queryText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void batchCreateByCourseChapter(Long courseEnrollmentId, Long sysUserId,
			Collection<CourseChapter> courseChapters) {

		if (courseChapters != null && courseChapters.isEmpty()) {
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

}
