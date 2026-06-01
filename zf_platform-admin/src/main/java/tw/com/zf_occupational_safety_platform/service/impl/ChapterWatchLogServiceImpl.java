package tw.com.zf_occupational_safety_platform.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import tw.com.zf_occupational_safety_platform.mapper.ChapterWatchLogMapper;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterWatchLog;
import tw.com.zf_occupational_safety_platform.service.ChapterWatchLogService;

/**
 * <p>
 * 章節觀看明細日誌 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-26
 */
@Service
public class ChapterWatchLogServiceImpl extends ServiceImpl<ChapterWatchLogMapper, ChapterWatchLog>
		implements ChapterWatchLogService {

	@Override
	public ChapterWatchLog get(Long chapterWatchLogId) {
		return baseMapper.selectById(chapterWatchLogId);
	}

	@Override
	public List<ChapterWatchLog> findByEnrollmentId(Long enrollmentId) {
		return baseMapper.selectByCourseEnrollmentId(enrollmentId);
	}

	@Override
	public Integer calculateTotalDurationSec(Long enrollmentId) {
		return baseMapper.selectTotalDurationSec(enrollmentId);
	}

	@Override
	public IPage<ChapterWatchLog> findPageByQuery(Page<ChapterWatchLog> pageInfo, String queryText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChapterWatchLog create(Long chapterProgressId, Long courseEnrollmentId, Long sysUserId,
			Long courseChapterId) {

		LocalDateTime now = LocalDateTime.now();

		ChapterWatchLog chapterWatchLog = new ChapterWatchLog();
		chapterWatchLog.setChapterProgressId(chapterProgressId);
		chapterWatchLog.setCourseEnrollmentId(courseEnrollmentId);
		chapterWatchLog.setCourseChapterId(courseChapterId);
		chapterWatchLog.setSysUserId(sysUserId);
		chapterWatchLog.setSessionStart(now);

		baseMapper.insert(chapterWatchLog);

		return chapterWatchLog;
	}

	@Override
	public void update(Long chapterWatchLogId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long chapterWatchLogId) {
		baseMapper.deleteById(chapterWatchLogId);
	}

	@Override
	public void removeByEnrollmentId(Long enrollmentId) {
		baseMapper.deleteByCourseEnrollmentId(enrollmentId);
	}

}
