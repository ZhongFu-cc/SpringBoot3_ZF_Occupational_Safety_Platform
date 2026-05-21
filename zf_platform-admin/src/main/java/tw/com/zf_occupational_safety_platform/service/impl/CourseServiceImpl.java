package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.mapper.CourseMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.service.CourseService;

/**
 * <p>
 * 課程主表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

	private final CourseConvert courseConvert;

	@Override
	public Course get(Long courseCategoryId) {
		return baseMapper.selectById(courseCategoryId);
	}

	@Override
	public Course create(AddCourseDTO addCourseDTO) {
		Course course = courseConvert.addDTOToEntity(addCourseDTO);
		baseMapper.insert(course);
		return course;
	}

	@Override
	public void update(PutCourseDTO putCourseDTO) {
		Course course = courseConvert.putDTOToEntity(putCourseDTO);
		baseMapper.updateById(course);
	}

	@Override
	public void remove(Long courseCategoryId) {
		baseMapper.deleteById(courseCategoryId);
	}
	
}
