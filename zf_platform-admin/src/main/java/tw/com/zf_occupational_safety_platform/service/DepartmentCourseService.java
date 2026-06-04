package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;

/**
 * <p>
 * 部門 x 公司課程表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface DepartmentCourseService extends IService<DepartmentCourse> {

	List<DepartmentCourse> findByDepartmentId(Long departmentId);
	
	void assignCourse2Department(Long departmentId , Collection<Long> courseIds);
	
	void removeByDepartment(Long departmentId);
	
}
