package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.ChpaterVideoConvert;
import tw.com.zf_occupational_safety_platform.mapper.ChapterVideoMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;
import tw.com.zf_occupational_safety_platform.service.ChapterVideoService;

/**
 * <p>
 * 章節影片 - 只有content_type 為video的才有 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class ChapterVideoServiceImpl extends ServiceImpl<ChapterVideoMapper, ChapterVideo>
		implements ChapterVideoService {

	private final ChpaterVideoConvert chpaterVideoConvert;

	@Override
	public ChapterVideo get(Long chapterVideoId) {
		return baseMapper.selectById(chapterVideoId);
	}

	@Override
	public ChapterVideo getByChapter(Long courseChapterId) {
		return baseMapper.selectByCourseChapterId(courseChapterId);
	}

	@Override
	public ChapterVideo create(AddChapterVideoDTO addChapterVideoDTO) {
		ChapterVideo chapterVideo = chpaterVideoConvert.addDTOToEntity(addChapterVideoDTO);
		baseMapper.insert(chapterVideo);
		return chapterVideo;
	}

	@Override
	public void update(PutChapterVideoDTO putChapterVideoDTO) {
		ChapterVideo chapterVideo = chpaterVideoConvert.putDTOToEntity(putChapterVideoDTO);
		baseMapper.updateById(chapterVideo);
	}

	@Override
	public void remove(Long chapterVideoId) {
		baseMapper.deleteById(chapterVideoId);
	}

}
