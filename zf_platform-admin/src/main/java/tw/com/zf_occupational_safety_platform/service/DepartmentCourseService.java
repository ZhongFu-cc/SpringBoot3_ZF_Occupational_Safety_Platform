package tw.com.zf_occupational_safety_platform.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

	/**
	 * 部門 x 課程的組成是唯一的<br>
	 * 用於判斷是否存在
	 * 
	 * @param departmentId
	 * @param companyCourseId
	 * @return
	 */
	boolean isExist(Long departmentId, Long companyCourseId);

	List<DepartmentCourse> findByDepartmentId(Long departmentId);

	List<DepartmentCourse> findByCompanyCourses(Collection<Long> companyCourseIds);

	IPage<DepartmentCourse> findPage(Page<DepartmentCourse> pageInfo);

	IPage<DepartmentCourse> findPage(Page<DepartmentCourse> pageInfo, Collection<Long> companyCourseIds);

	void addCourse2Department(Long departmentId, Long companyCourseId);

	/**
	 * 查詢條件中的部門<br>
	 * 並成為以 departmentId為key , companyCourseIds為 value的映射對象
	 * 
	 * @param departmentIds
	 * @return
	 */
	Map<Long, List<Long>> mapByDepartmentId(List<Long> departmentIds);

	void assignCourse2Department(Long departmentId, Collection<Long> courseIds);

	void removeByDepartment(Long departmentId);

}
