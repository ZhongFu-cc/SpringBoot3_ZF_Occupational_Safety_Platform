package tw.com.zf_occupational_safety_platform.system.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.MissingRequestParameterException;
import tw.com.zf_occupational_safety_platform.exception.PermissionException;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeExcel;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.exception.SysUserException;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysRole;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserRoleService;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 負責企業管理者操作的 管理層
 * 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyManager {

	// 企業管理者 為 企業員工 創建的角色權限符
	private static final String ROLE_KEY = "employee";

	private final SysUserService sysUserService;
	private final SysUserConvert sysUserConvert;
	private final SysUserRoleService sysUserRoleService;
	private final SysRoleService sysRoleService;
	private final DepartmentService departmentService;
	private final DepartmentCourseService departmentCourseService;
	private final CompanyCourseService companyCourseService;
	private final CourseEnrollmentService courseEnrollmentService;

	// 指派 學習任務

	// 查看 部門內員工課程達成率

	// 匯出 報表。

	/**
	 * ----------------- 企業員工管理 --------------------
	 */

	/**
	 * 查詢企業員工
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 * @return
	 */
	public SysUser getEmployee(Long sysUserId, SysUserVO sysUserVO) {

		SysUser sysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (sysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!sysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		return sysUser;

	}

	/**
	 * 分頁查詢 - 公司內員工<br>
	 * 排除同級管理者
	 * 
	 * @param pageInfo
	 * @param sysUserVO
	 * @param queryText
	 * @return
	 */
	public IPage<SysUser> findEmployee(Page<SysUser> pageInfo, SysUserVO sysUserVO, String queryText) {
		return sysUserService.findByCompany(pageInfo, sysUserVO.getParentId(), sysUserVO.getCompanyId(), queryText);
	}

	/**
	 * 匯入Excel 批量新增 員工資料
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void importExcel(MultipartFile file, SysUserVO operator) throws IOException {

		Long companyId = operator.getCompanyId();
		String companyName = operator.getCompanyName();

		List<Department> departments = departmentService.findByCompany(companyId);
		Map<String, Long> departmentMap = departments.stream()
				.collect(Collectors.toMap(Department::getName, Department::getDepartmentId));

		// ========== Phase 1: validate ==========
		validateExcel(file, departmentMap);

		// ========== Phase 2: import ==========
		importExcelData(file, companyId, companyName, departmentMap);

	}

	/**
	 * 第一次遍歷，驗證Excel內的部門皆為合規值
	 * 
	 * @param file          excel 檔案
	 * @param departmentMap 部門名:部門ID Map
	 * @throws IOException
	 */
	private void validateExcel(MultipartFile file, Map<String, Long> departmentMap) throws IOException {

		AtomicBoolean hasError = new AtomicBoolean(false);

		EasyExcel.read(file.getInputStream(), EmployeeExcel.class, new ReadListener<EmployeeExcel>() {

			@Override
			public void invoke(EmployeeExcel row, AnalysisContext context) {
				Long deptId = departmentMap.get(row.getDepartment());
				if (deptId == null) {
					int rowNum = context.readRowHolder().getRowIndex() + 1;
					throw new SysUserException("不合規的部門 at row " + rowNum + ": " + row.getDepartment());
				}
			}

			@Override
			public void doAfterAllAnalysed(AnalysisContext context) {
				// pass
			}

		}).sheet().doRead();
	}

	/**
	 * 
	 * 
	 * @param file          excel 檔案
	 * @param companyId     公司ID
	 * @param companyName   公司名
	 * @param departmentMap 部門名:部門ID Map
	 * @throws IOException
	 */
	private void importExcelData(MultipartFile file, Long companyId, String companyName,
			Map<String, Long> departmentMap) throws IOException {

		EasyExcel.read(file.getInputStream(), EmployeeExcel.class, new ReadListener<EmployeeExcel>() {

			private static final int BATCH_SIZE = 500;
			private final List<SysUser> batch = new ArrayList<>();

			@Override
			public void invoke(EmployeeExcel row, AnalysisContext context) {

				SysUser user = sysUserConvert.employeeExcelToEntity(row);
				user.setCompanyId(companyId);
				user.setCompanyName(companyName);
				user.setDepartmentId(departmentMap.get(row.getDepartment()));
				batch.add(user);

				if (batch.size() >= BATCH_SIZE) {
					sysUserService.saveBatch(batch);
					batch.clear();
				}
			}

			@Override
			public void doAfterAllAnalysed(AnalysisContext context) {
				if (!batch.isEmpty()) {
					sysUserService.saveBatch(batch);
				}
			}

		}).sheet().doRead();
	}

	/**
	 * 創建 企業員工用戶
	 * 
	 * @param addUserDTO
	 */
	public void createEmployee(AddSysUserDTO addSysUserDTO, SysUserVO sysUserVO) {

		if (addSysUserDTO.getDepartmentId() == null) {
			throw new MissingRequestParameterException("部門不可為空");
		}

		// 資料轉換後，添加parentId，並新增
		SysUser sysUser = sysUserConvert.addDTOToEntity(addSysUserDTO);
		sysUser.setParentId(sysUserVO.getSysUserId());
		// 不管前端companyId傳什麼，都以當前操作者的companyId為準
		sysUser.setCompanyId(sysUserVO.getCompanyId());

		sysUserService.save(sysUser);

		// 透過roleKey 拿到角色ID
		SysRole sysRole = sysRoleService.getByRoleKey(ROLE_KEY);

		// 為新的企業員工添加角色
		sysUserRoleService.assignRole2User(sysUser.getSysUserId(), sysRole.getSysRoleId());

		// 創建完後，查看目前部門有無分配課程，如果有則自動幫他報名
		List<DepartmentCourse> departmentCourses = departmentCourseService
				.findByDepartmentId(sysUser.getDepartmentId());
		List<Long> companyCourseIds = departmentCourses.stream().map(DepartmentCourse::getCompanyCourseId).toList();
		List<CompanyCourse> companyCourses = companyCourseService.findByIds(companyCourseIds);
		// 如果沒有企業課程，直接return
		if (companyCourses.isEmpty()) {
			return;
		}
		Set<Long> courseIds = companyCourses.stream().map(CompanyCourse::getCourseId).collect(Collectors.toSet());

		courseEnrollmentService.batchCreate(sysUser.getSysUserId(), courseIds);
	}

	/**
	 * 更新 企業員工用戶
	 * 
	 * @param putSysUserDTO
	 * @param sysUserVO
	 */
	public void updateEmployee(PutSysUserDTO putSysUserDTO, SysUserVO sysUserVO) {

		// 獲取當前用戶
		SysUser currentSysUser = sysUserService.get(putSysUserDTO.getSysUserId());

		// 父級ID == null , 最大權限者不給予操作
		if (currentSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 當前用戶的companyId 與 token解析下當前操作者的companyId 不一致則拋出錯誤信息
		if (!currentSysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 目前僅更新基本資料，後續有其他需求再開發
		sysUserService.update(putSysUserDTO);
	}

	/**
	 * 批量更新 員工資料
	 */

	/**
	 * 刪除 企業員工用戶
	 * 
	 * @param sysUserId
	 * @param sysUserVO
	 */
	public void removeEmployee(Long sysUserId, SysUserVO sysUserVO) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!targetSysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		// 移除此用戶目前擁有的角色關係
		sysUserRoleService.removeByUserId(sysUserId);

		// 目前僅直接刪除資料，後續有其他需求再開發
		sysUserService.remove(sysUserId);
	}

	/**
	 * 更改企業員工狀態<br>
	 * 啟用/禁用 用戶
	 * 
	 * @param sysUserId
	 * @param status
	 * @param operator
	 */
	public void switchEmployeeStatus(Long sysUserId, CommonStatusEnum status, SysUserVO operator) {
		SysUser targetSysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (targetSysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!targetSysUser.getCompanyId().equals(operator.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		sysUserService.updateCompanyUserStatus(sysUserId, status);
	}

}
