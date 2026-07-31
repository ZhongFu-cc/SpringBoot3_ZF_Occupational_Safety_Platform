package tw.com.zf_occupational_safety_platform.system.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeExcel;
import tw.com.zf_occupational_safety_platform.service.CompanyCourseService;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.DepartmentCourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.service.StagingSysUserService;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.exception.SysUserException;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.StagingCheckResultDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.EmployeeVO;
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
	private final StagingSysUserService stagingSysUserService;
	private final SysUserConvert sysUserConvert;
	private final SysUserRoleService sysUserRoleService;
	private final SysRoleService sysRoleService;
	private final DepartmentService departmentService;
	private final DepartmentCourseService departmentCourseService;
	private final CompanyCourseService companyCourseService;
	private final CourseEnrollmentService courseEnrollmentService;

	/**
	 * 根據 課程分派規則，為所有<br>
	 * 未持有課程(含課程認證過期的)企業員工<br>
	 * 分配學習任務
	 * 
	 */
	public void assignCourse2Employee() {
		// 查詢部門 x 企業課程關聯
	}

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
	public EmployeeVO getEmployee(Long sysUserId, SysUserVO sysUserVO) {

		SysUser sysUser = sysUserService.get(sysUserId);

		// 父級ID == null , 最大權限者不給予操作
		if (sysUser.getParentId() == null) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		if (!sysUser.getCompanyId().equals(sysUserVO.getCompanyId())) {
			throw new PermissionException("您無權操作此資源，該資料不屬於您的負責範圍。");
		}

		EmployeeVO employeeVO = sysUserConvert.entityToEmployeeVO(sysUser);

		// 補上部門名稱，員工可能尚未分配部門
		if (sysUser.getDepartmentId() != null) {
			Department department = departmentService.getByIdAndCompany(sysUser.getDepartmentId(),
					sysUser.getCompanyId());
			if (department != null) {
				employeeVO.setDepartmentName(department.getName());
			}
		}

		return employeeVO;

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
	public IPage<EmployeeVO> findEmployee(Page<SysUser> pageInfo, SysUserVO sysUserVO, String queryText) {

		IPage<SysUser> userPage = sysUserService.findByCompany(pageInfo, sysUserVO.getParentId(),
				sysUserVO.getCompanyId(), queryText);

		// 公司的部門數量有限，一次撈出建立映射，避免每筆員工都查一次DB
		Map<Long, String> departmentNameMap = departmentService.findByCompany(sysUserVO.getCompanyId()).stream()
				.collect(Collectors.toMap(Department::getDepartmentId, Department::getName));

		List<EmployeeVO> vos = userPage.getRecords().stream().map(sysUser -> {
			EmployeeVO vo = sysUserConvert.entityToEmployeeVO(sysUser);
			vo.setDepartmentName(departmentNameMap.get(sysUser.getDepartmentId()));
			return vo;
		}).toList();

		Page<EmployeeVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
		voPage.setRecords(vos);
		return voPage;
	}

	/**
	 * 匯入Excel 批量新增 員工資料
	 * * @param file
	 * 
	 * @throws IOException
	 */
	@Transactional
	public void importExcel(MultipartFile file, SysUserVO operator) throws IOException {

		Long companyId = operator.getCompanyId();
		String batchId = UUID.randomUUID().toString();

		List<Department> departments = departmentService.findByCompany(companyId);
		Map<String, Long> departmentMap = departments.stream()
				.collect(Collectors.toMap(Department::getName, Department::getDepartmentId));

		// 只讀一次 Excel：同時完成部門校驗，並直接寫入擴充後的臨時表
		save2StagingWithValidate(file, batchId, operator, departmentMap);

		// 資料庫校驗：利用索引在 DB 內快速查找重複
		validateStaging(batchId, companyId);

		// SQL內轉存：由 DB 內部執行 INSERT INTO ... SELECT
		sysUserService.insertFromStaging(batchId);

		// Phase 5: cleanup
		stagingSysUserService.removeByBatchId(batchId);
	}

	/**
	 * 讀取 Excel、驗證部門並寫入臨時表
	 */
	private void save2StagingWithValidate(MultipartFile file, String batchId, SysUserVO operator,
			Map<String, Long> departmentMap) throws IOException {

		// 批次大小 1000 - 2000 
		List<StagingSysUser> batch = new ArrayList<>(1000);

		EasyExcel.read(file.getInputStream(), EmployeeExcel.class, new ReadListener<EmployeeExcel>() {

			@Override
			public void invoke(EmployeeExcel row, AnalysisContext context) {
				int rowNum = context.readRowHolder().getRowIndex() + 1;
				Long deptId = departmentMap.get(row.getDepartment());

				// 在寫入臨時表前先做部門校驗
				if (deptId == null) {
					throw new SysUserException("Invalid department at row " + rowNum + ": " + row.getDepartment());
				}

				// 封裝至臨時表（臨時表需包含正式表所需欄位）
				StagingSysUser s = sysUserConvert.employeeExcelToStaging(row);
				s.setBatchId(batchId);
				s.setParentId(operator.getSysUserId());
				s.setDepartmentId(deptId);
				s.setCompanyId(operator.getCompanyId());
				s.setCompanyName(operator.getCompanyName());

				batch.add(s);

				if (batch.size() >= 1000) {
					stagingSysUserService.saveBatch(batch);
					batch.clear();
				}
			}

			@Override
			public void doAfterAllAnalysed(AnalysisContext context) {
				if (!batch.isEmpty()) {
					stagingSysUserService.saveBatch(batch);
				}
			}
		}).sheet().doRead();
	}

	/**
	 * 判斷臨時表中 有無重複的account or email<br>
	 * 判斷已有用戶中 有無重複的account or email
	 * * @param batchId
	 * 
	 * @param companyId
	 */
	private void validateStaging(String batchId, Long companyId) {

		// 一口氣完成所有校驗，全表需 1 次網路請求，且有 LIMIT 1 短路特性
		StagingCheckResultDTO result = stagingSysUserService.executeStagingValidation(batchId);

		if (result != null) {
			// 清空臨時表，表示這次匯入失敗
			stagingSysUserService.removeByBatchId(batchId);

			switch (result.getDupType()) {
			case "EXCEL_ACCOUNT":
				throw new SysUserException("Duplicate account in Excel: " + result.getAccount());
			case "EXCEL_EMAIL":
				throw new SysUserException("Duplicate email in Excel: " + result.getEmail());
			case "DB_DUP":
				throw new SysUserException("Duplicate with DB: " + result.getAccount() + " / " + result.getEmail());
			default:
				throw new SysUserException("Unknown validation error.");
			}
		}
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
		sysUser.setCompanyName(sysUserVO.getCompanyName());

		boolean accountExist = sysUserService.validAccountExist(sysUser);
		if (accountExist) {
			throw new SysUserException("系統內已有重複帳號，請更換帳號使用");
		}
		boolean emailExist = sysUserService.validEmailExist(sysUser);
		if (emailExist) {
			throw new SysUserException("系統內已有重複E-Mail，請更換E-Mail使用");
		}

		sysUserService.save(sysUser);

		// 透過roleKey 拿到角色ID
		SysRole sysRole = sysRoleService.getByRoleKey(ROLE_KEY);

		// 為新的企業員工添加角色
		sysUserRoleService.assignRole2User(sysUser.getSysUserId(), sysRole.getSysRoleId());

		// 創建完後，查看目前部門有無分配課程，如果有則自動幫他報名
		List<DepartmentCourse> departmentCourses = departmentCourseService
				.findByDepartmentId(sysUser.getDepartmentId());
		
		// 如果沒有部門課程，直接return
		if (departmentCourses.isEmpty()) {
			return;
		}

		List<Long> companyCourseIds = departmentCourses.stream().map(DepartmentCourse::getCompanyCourseId).toList();
		List<CompanyCourse> companyCourses = companyCourseService.findByIds(companyCourseIds);
		// 如果沒有企業課程，直接return
		if (companyCourses.isEmpty()) {
			return;
		}
		Set<Long> courseIds = companyCourses.stream().map(CompanyCourse::getCourseId).collect(Collectors.toSet());

		courseEnrollmentService.batchCreate(sysUser, courseIds);
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
