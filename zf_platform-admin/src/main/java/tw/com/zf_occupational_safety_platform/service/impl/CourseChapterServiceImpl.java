package tw.com.zf_occupational_safety_platform.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseChapterConvert;
import tw.com.zf_occupational_safety_platform.enums.ChapterContentTypeEnum;
import tw.com.zf_occupational_safety_platform.mapper.CourseChapterMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseChapterVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;
import tw.com.zf_occupational_safety_platform.service.CourseChapterService;
import tw.com.zf_occupational_safety_platform.utils.TreeUtil;

/**
 * <p>
 * 課程章節與自定義表單綁定結構表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Service
@RequiredArgsConstructor
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter>
		implements CourseChapterService {

	private final CourseChapterConvert courseChapterConvert;

	@Override
	public boolean existQuizChapter(Long courseId) {
		if (baseMapper.countByQuizChapter(courseId) > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void clearVideoUrl(Long courseChapterId) {
		baseMapper.clearVideoUrl(courseChapterId);
	}

	/** --------------------------------------------- */

	@Override
	public CourseChapter get(Long courseChapterId) {
		return baseMapper.selectById(courseChapterId);
	}
	

	@Override
	public List<CourseChapter> findAllNode(Long courseChapterId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CourseChapter> findNonDirectoryByCourseId(Long courseId) {
		// 查詢課程的所有單元
		List<CourseChapter> courseChapters = baseMapper.selectByCourseId(courseId);

		// 過濾不是DIRECTORY類型的chapter
		return courseChapters.stream()
				.filter(chapter -> !ChapterContentTypeEnum.DIRECTORY.equals(chapter.getContentType()))
				.toList();

	}

	@Override
	public List<CourseChapter> findNonDirectoryByCourseIds(Collection<Long> allCourseIds) {
		if (allCourseIds == null || allCourseIds.isEmpty()) {
			Collections.emptyList();
		}

		// 查詢課程的所有單元
		List<CourseChapter> courseChapters = baseMapper.selectByCourseId(allCourseIds);

		// 過濾不是DIRECTORY類型的chapter
		return courseChapters.stream()
				.filter(chapter -> !ChapterContentTypeEnum.DIRECTORY.equals(chapter.getContentType()))
				.toList();
	}

	@Override
	public List<CourseChapterVO> findTreeSourceByCourseId(Long courseId) {
		// 查詢課程的所有單元
		List<CourseChapter> courseChapters = baseMapper.selectByCourseId(courseId);

		// 轉換成vo對象List
		return courseChapters.stream().map(courseChapter -> {
			CourseChapterVO vo = courseChapterConvert.entityToVO(courseChapter);
			return vo;
		}).toList();
		
	}

	@Override
	public List<CourseChapterVO> findTreeByCourseId(Long courseId) {
		// 查詢課程的所有單元
		List<CourseChapter> courseChapters = baseMapper.selectByCourseId(courseId);

		// 轉換成vo對象
		List<CourseChapterVO> voList = courseChapters.stream().map(courseChapter -> {
			CourseChapterVO vo = courseChapterConvert.entityToVO(courseChapter);
			return vo;
		}).toList();

		// 構建vo樹狀結構列表
		return TreeUtil.buildTree(voList);

	}

	@Override
	public CourseChapter create(AddCourseChapterDTO addCourseChapterDTO) {
		CourseChapter courseChapter = courseChapterConvert.addDTOToEntity(addCourseChapterDTO);
		baseMapper.insert(courseChapter);
		return courseChapter;
	}

	@Override
	public void update(PutCourseChapterDTO putCourseChapterDTO) {
		CourseChapter courseChapter = courseChapterConvert.putDTOToEntity(putCourseChapterDTO);
		baseMapper.updateById(courseChapter);
	}

	@Override
	public void remove(Long courseChapterId) {
		baseMapper.deleteById(courseChapterId);
	}


}
