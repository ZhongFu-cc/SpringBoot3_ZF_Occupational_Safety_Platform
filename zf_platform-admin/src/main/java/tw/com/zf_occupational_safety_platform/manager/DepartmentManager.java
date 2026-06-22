package tw.com.zf_occupational_safety_platform.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.controller.DepartmentController.AddDepartmentCourse;
import tw.com.zf_occupational_safety_platform.exception.DepartmentException;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 部門 - 管理層
 */
@Component
@RequiredArgsConstructor
public class DepartmentManager {

	private final SysUserService sysUserService;
	private final DepartmentService departmentService;
	private final DepartmentCourseService departmentCourseService;
	private final CompanyCourseService companyCourseService;
	private final CourseEnrollmentService courseEnrollmentService;

	/**
	 * 拿到操作者，所能操作的部門 單一對象
	 * 
	 * @param departmentId
	 * @param operator
	 * @return
	 */
	public Department getDepartment(Long departmentId, SysUserVO operator) {
		return departmentService.getByIdAndCompany(departmentId, operator.getCompanyId());
	}

	/**
	 * 拿到操作者，所能操作的部門 分頁對象
	 * 
	 * @param pageInfo  分頁資訊
	 * @param queryText 查詢條件
	 * @param operator  操作者
	 * @return
	 */
	public IPage<Department> findDepartmentPage(Page<Department> pageInfo, String queryText, SysUserVO operator) {
		return departmentService.findPageByQuery(pageInfo, queryText, operator.getCompanyId());
	}

	/**
	 * 創建部門，companyId 以當前操作者為主
	 * 
	 * @param addDepartmentDTO
	 * @param operator
	 */
	public void createDepartment(AddDepartmentDTO addDepartmentDTO, SysUserVO operator) {
		addDepartmentDTO.setCompanyId(operator.getCompanyId());
		departmentService.create(addDepartmentDTO);
	}

	/**
	 * 修改部門，companyId 已當前操作者為主
	 * 
	 * @param putDepartmentDTO
	 * @param operator
	 */
	public void updateDepartment(PutDepartmentDTO putDepartmentDTO, SysUserVO operator) {

		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(putDepartmentDTO.getDepartmentId());

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		departmentService.update(putDepartmentDTO);
	}

	/**
	 * 移除部門
	 * 
	 * @param departmentId
	 * @param operator
	 */
	public void removeDepartment(Long departmentId, SysUserVO operator) {

		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(departmentId);

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 先判斷employee , 都有更改成其他部門了才往下進行操作
		long countByDepartment = sysUserService.countByDepartment(departmentId);
		if (countByDepartment != 0) {
			throw new DepartmentException("請將部門人員清空，再進行刪除操作");
		}

		// 移除部門 x 課程的關聯
		departmentCourseService.removeByDepartment(departmentId);

		// 移除部門本身
		departmentService.remove(departmentId);
	}

	/**
	 * 分配/移除 部門x課程
	 * 
	 * @param addDepartmentCourse
	 * @param operator
	 */
	public void assignCourse2Department(AddDepartmentCourse addDepartmentCourse, SysUserVO operator) {
		// 獲取當前部門資料
		Department currentDepartment = departmentService.get(addDepartmentCourse.departmentId());

		if (!currentDepartment.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 根據公司ID 和 課程ID去查詢是否屬於企業的課程
		List<CompanyCourse> companyCourses = companyCourseService
				.findByIdsAndCompany(addDepartmentCourse.companyCourseId(), operator.getCompanyId());
		if (companyCourses.isEmpty()) {
			return;
		}

		// 提取 企業課程ID 去重後進行分配
		Set<Long> companyCourseIds = companyCourses.stream()
				.map(CompanyCourse::getCompanyCourseId)
				.collect(Collectors.toSet());
		departmentCourseService.assignCourse2Department(addDepartmentCourse.departmentId(), companyCourseIds);

	}

	/**
	 * 為企業所有部門員工，報名應上的課程
	 * 
	 * @param operator
	 */
	public void oneClickEnrollment(SysUserVO operator) {
		// 1.查詢所有部門
		List<Department> departments = departmentService.findByCompany(operator.getCompanyId());
		List<Long> deptIds = departments.stream().map(Department::getDepartmentId).toList();

		// 2.查詢部門內所有的用戶(企業員工)
		List<SysUser> employees = sysUserService.findByDepartments(deptIds);

		// 3.查詢部門要上的課，得到以 departmentId為key , companyCourseIds為 value的映射對象
		Map<Long, List<Long>> departmentCompanyCourseMap = departmentCourseService.mapByDepartmentId(deptIds);

		// 4. 將 companyCourseId 轉換為實際的 courseId
		// 4.1 收集所有去重的 companyCourseId
		List<Long> allCompanyCourseIds = departmentCompanyCourseMap.values()
				.stream()
				.flatMap(List::stream)
				.distinct()
				.toList();

		// 初始化一個 以 departmentId為key , courseIds為value的 Map
		Map<Long, List<Long>> departmentCourseMap = new HashMap<>();

		if (!allCompanyCourseIds.isEmpty()) {
			// 4.2 查詢 CompanyCourse
			List<CompanyCourse> companyCourses = companyCourseService.findByIds(allCompanyCourseIds);

			// 4.3 建立 companyCourseId -> courseId 的映射關係
			Map<Long, Long> companyCourseToCourseMap = companyCourses.stream()
					.collect(Collectors.toMap(CompanyCourse::getCompanyCourseId, CompanyCourse::getCourseId));

			// 4.4 組裝成 departmentId 為 key, courseIds 為 value 的 map 對象
			departmentCourseMap = departmentCompanyCourseMap.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
							entry -> entry.getValue()
									.stream()
									.map(companyCourseToCourseMap::get) // 轉換為 courseId
									.filter(Objects::nonNull) // 排除可能因資料不一致導致的 null 值
									.distinct() // 如果有多個 companyCourse 對應同個 courseId 可去重
									.toList()));
		}

		// 接下來就可以使用 departmentCourseMap (Map<Long, List<Long>>) 來為員工進行報名了

		// ...

	}

}
