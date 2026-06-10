package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;

/**
 * <p>
 * 課程主表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseService extends IService<Course> {

	Course get(Long courseId);
	
	List<Course> findByIds(Collection<Long> courseIds);

	List<Course> findByCategoryIds(Collection<Long> courseCategoryIds);
	
	List<Course> findByQuery(String queryText);
	
	Map<Long,Course> findCourseIdMapByQuery(String queryText);
	
	IPage<Course> findPageByQuery(Page<Course> pageInfo, Long courseCategoryId, String queryText);

	Course create(AddCourseDTO addCourseDTO);

	void update(PutCourseDTO putCourseDTO);

	void remove(Long courseId);

}
