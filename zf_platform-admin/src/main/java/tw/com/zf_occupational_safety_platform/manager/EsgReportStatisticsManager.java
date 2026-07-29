package tw.com.zf_occupational_safety_platform.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgDepartmentTrainingVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgLearningDetailVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgTrainingChartVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.EsgTrainingKpiVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningStatusDistributionVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.pojo.excel.EsgLearningDetailExcel;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * ESG 報告統計 - 管理層<br>
 * 提供 HR 製作企業 ESG 報告書所需的年度教育訓練指標與圖表<br>
 * 年度一律以 course_enrollment.enrolled_at (報名時間) 的年份認定；<br>
 * 所有人頭相關的分子分母，一律以「當前活躍員工」為同一母體，避免出現超過 100% 的比率
 */
@Component
@RequiredArgsConstructor
public class EsgReportStatisticsManager {

	private final CourseEnrollmentService courseEnrollmentService;
	private final SysUserService sysUserService;
	private final DepartmentService departmentService;
	private final CourseService courseService;

	/**
	 * 獲取企業 年度教育訓練 ESG KPI<br>
	 * 一次回傳四個指標：年度受訓總時數、人均受訓時數、培訓員工總覆蓋率、課程總完課率
	 *
	 * @param operator 操作者 (企業管理者/HR)
	 * @param year     統計年度，非必要，null 則取當年
	 * @return
	 */
	public EsgTrainingKpiVO getAnnualTrainingKpi(SysUserVO operator, Integer year) {

		EsgTrainingKpiVO vo = new EsgTrainingKpiVO();

		// 1.決定統計年度，未指定則取當年
		int targetYear = resolveYear(year);
		vo.setYear(targetYear);

		// 2.取得全公司「當前活躍」的員工ID集合
		Set<Long> activeUserIds = findActiveEmployees(operator).stream()
				.map(SysUser::getSysUserId)
				.collect(Collectors.toSet());

		int activeEmployeeCount = activeUserIds.size();
		vo.setActiveEmployeeCount(activeEmployeeCount);

		// 3.取得該公司「該年度報名」的所有報名資料
		List<CourseEnrollment> yearEnrollments = findYearEnrollments(operator.getCompanyId(), targetYear);

		// 該年度尚無任何報名資料，返回零值 (活躍人數仍照實回傳)
		if (yearEnrollments.isEmpty()) {
			vo.setTotalTrainingHours(0.0);
			vo.setAverageHoursPerEmployee(0.0);
			vo.setTrainingCoverageRate(0.0);
			vo.setOverallCompletionRate(0.0);
			vo.setTrainedEmployeeCount(0);
			vo.setTotalEnrollmentCount(0);
			vo.setCompletedEnrollmentCount(0);
			return vo;
		}

		/**
		 * 指標一：年度受訓總時數 (該年度學習秒數加總 / 3600)
		 */
		double totalHours = sumHours(yearEnrollments);
		vo.setTotalTrainingHours(round1(totalHours));

		/**
		 * 指標二：人均受訓時數 (年度受訓總時數 / 全公司活躍總人數)<br>
		 * 以未四捨五入的總時數相除，最後才進位，避免二次捨入誤差
		 */
		double averageHours = activeEmployeeCount > 0 ? totalHours / activeEmployeeCount : 0.0;
		vo.setAverageHoursPerEmployee(round1(averageHours));

		/**
		 * 指標三：培訓員工總覆蓋率 (該年度有報名紀錄的不重複人數 / 全公司活躍總人數)<br>
		 * 分子刻意與 activeUserIds 取交集，讓分子分母同母體。<br>
		 * 否則「當年有報名、之後被停用」的員工會使分子大於分母，出現 >100% 的覆蓋率
		 */
		long trainedEmployeeCount = yearEnrollments.stream()
				.map(CourseEnrollment::getSysUserId)
				.distinct()
				.filter(activeUserIds::contains)
				.count();
		vo.setTrainedEmployeeCount((int) trainedEmployeeCount);

		double coverageRate = activeEmployeeCount > 0 ? (double) trainedEmployeeCount / activeEmployeeCount * 100 : 0.0;
		vo.setTrainingCoverageRate(round1(coverageRate));

		/**
		 * 指標四：課程總完課率 (該年度 completed 筆數 / 該年度報名總筆數)<br>
		 * 分母為該年度全部報名筆數(含 cancelled)。<br>
		 * 註：既有的 CourseEnrollmentMapper.getCompanyTrainingSummary 是排除 cancelled 的，<br>
		 * 若日後要對齊該口徑，在此加上 .filter(e -> e.getStatus() != CourseStatusEnum.CANCELLED) 即可
		 */
		int totalEnrollmentCount = yearEnrollments.size();
		long completedEnrollmentCount = yearEnrollments.stream()
				.filter(enrollment -> CourseStatusEnum.COMPLETED.equals(enrollment.getStatus()))
				.count();

		vo.setTotalEnrollmentCount(totalEnrollmentCount);
		vo.setCompletedEnrollmentCount((int) completedEnrollmentCount);

		double completionRate = (double) completedEnrollmentCount / totalEnrollmentCount * 100;
		vo.setOverallCompletionRate(round1(completionRate));

		return vo;
	}

	/**
	 * 獲取企業 年度教育訓練 ESG 圖表<br>
	 * 左圖：各部門培訓多元性與資源分配 (人均時數 + 完課率)<br>
	 * 右圖：培訓實質成效分佈 (按人頭，四類互斥)
	 *
	 * @param operator 操作者 (企業管理者/HR)
	 * @param year     統計年度，非必要，null 則取當年
	 * @return
	 */
	public EsgTrainingChartVO getAnnualTrainingChart(SysUserVO operator, Integer year) {

		EsgTrainingChartVO vo = new EsgTrainingChartVO();

		// 1.決定統計年度，未指定則取當年
		int targetYear = resolveYear(year);
		vo.setYear(targetYear);

		// 2.取得全公司「當前活躍」的員工
		List<SysUser> activeEmployees = findActiveEmployees(operator);

		// 3.取得該公司「該年度報名」的資料，並依員工ID分組
		// 後續一律以「活躍員工」反查其報名紀錄，故已停用員工的資料自然被排除，分子分母恆為同母體
		Map<Long, List<CourseEnrollment>> userEnrollmentMap = findYearEnrollments(operator.getCompanyId(), targetYear)
				.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		// 4.組裝左圖 (各部門培訓多元性與資源分配)
		vo.setDepartmentTrainings(buildDepartmentTrainings(operator.getCompanyId(), activeEmployees, userEnrollmentMap));

		// 5.組裝右圖 (培訓實質成效分佈)
		vo.setEffectivenessDistribution(buildEffectivenessDistribution(activeEmployees, userEnrollmentMap));

		return vo;
	}

	/**
	 * 組裝左圖：各部門培訓多元性與資源分配<br>
	 * 以公司內「所有部門」為基準列出，當年完全無人受訓的部門也會出現(顯示 0)，<br>
	 * 因為「某部門完全沒受訓」本身就是 ESG 報告書要呈現的關鍵發現
	 *
	 * @param companyId         公司ID
	 * @param activeEmployees   全公司活躍員工
	 * @param userEnrollmentMap 該年度報名資料 Map (key: sysUserId)
	 */
	private List<EsgDepartmentTrainingVO> buildDepartmentTrainings(Long companyId, List<SysUser> activeEmployees,
			Map<Long, List<CourseEnrollment>> userEnrollmentMap) {

		List<Department> departments = departmentService.findByCompany(companyId);
		if (departments == null || departments.isEmpty()) {
			return Collections.emptyList();
		}

		// 活躍員工依「現況部門」分組。
		// 刻意採用 sys_user.department_id 而非 course_enrollment.department_id，
		// 後者是報名當下的快照，員工轉調部門後會使分子分母歸屬到不同部門，算出 >100% 的完課率
		Map<Long, List<SysUser>> deptEmployeeMap = activeEmployees.stream()
				.filter(employee -> employee.getDepartmentId() != null)
				.collect(Collectors.groupingBy(SysUser::getDepartmentId));

		return departments.stream().map(department -> {

			List<SysUser> deptEmployees = deptEmployeeMap.getOrDefault(department.getDepartmentId(),
					Collections.emptyList());
			int deptEmployeeCount = deptEmployees.size();

			// 累計該部門的受訓秒數與完訓人數
			List<CourseEnrollment> deptEnrollments = new ArrayList<>();
			int completedEmployeeCount = 0;
			for (SysUser employee : deptEmployees) {
				List<CourseEnrollment> enrollments = userEnrollmentMap.getOrDefault(employee.getSysUserId(),
						Collections.emptyList());
				deptEnrollments.addAll(enrollments);
				if (isFullyCompleted(enrollments)) {
					completedEmployeeCount++;
				}
			}

			double deptTotalHours = sumHours(deptEnrollments);
			double averageHours = deptEmployeeCount > 0 ? deptTotalHours / deptEmployeeCount : 0.0;
			double completionRate = deptEmployeeCount > 0 ? (double) completedEmployeeCount / deptEmployeeCount * 100
					: 0.0;

			EsgDepartmentTrainingVO deptVO = new EsgDepartmentTrainingVO();
			deptVO.setDepartmentId(department.getDepartmentId());
			deptVO.setDepartmentName(department.getName());
			deptVO.setActiveEmployeeCount(deptEmployeeCount);
			deptVO.setTotalTrainingHours(round1(deptTotalHours));
			deptVO.setAverageHoursPerEmployee(round1(averageHours));
			deptVO.setCompletedEmployeeCount(completedEmployeeCount);
			deptVO.setCompletionRate(round1(completionRate));
			return deptVO;

		}).sorted(Comparator.comparing(EsgDepartmentTrainingVO::getAverageHoursPerEmployee).reversed()).toList();
	}

	/**
	 * 組裝右圖：培訓實質成效分佈 (按人頭，四類互斥)<br>
	 * 分母為全公司活躍員工數，因此「當年完全沒被指派課程」的人會落在「未開始」，<br>
	 * 可與 KPI 的培訓員工總覆蓋率互相印證
	 *
	 * @param activeEmployees   全公司活躍員工
	 * @param userEnrollmentMap 該年度報名資料 Map (key: sysUserId)
	 */
	private List<LearningStatusDistributionVO> buildEffectivenessDistribution(List<SysUser> activeEmployees,
			Map<Long, List<CourseEnrollment>> userEnrollmentMap) {

		int totalUsers = activeEmployees.size();
		int completedUsers = 0; // 有效完訓：當年被分配的課全部 completed
		int expiredUsers = 0; // 已失效：尚未完訓，且任一門課證照已過期，需重新受訓
		int notStartedUsers = 0; // 未開始：完全沒被指派，或被分配的課全都還沒點開
		int inProgressUsers = 0; // 學習中未達標：其餘

		for (SysUser employee : activeEmployees) {
			List<CourseEnrollment> enrollments = userEnrollmentMap.getOrDefault(employee.getSysUserId(),
					Collections.emptyList());

			// 判定順序即為優先序，四類互斥
			if (isFullyCompleted(enrollments)) {
				completedUsers++;
			} else if (enrollments.stream().anyMatch(e -> CourseStatusEnum.EXPIRED.equals(e.getStatus()))) {
				expiredUsers++;
			} else if (enrollments.isEmpty()
					|| enrollments.stream().allMatch(e -> CourseStatusEnum.NOT_STARTED.equals(e.getStatus()))) {
				notStartedUsers++;
			} else {
				inProgressUsers++;
			}
		}

		List<LearningStatusDistributionVO> distributions = new ArrayList<>();
		distributions.add(createDistributionVO("有效完訓", completedUsers, totalUsers));
		distributions.add(createDistributionVO("已失效", expiredUsers, totalUsers));
		distributions.add(createDistributionVO("學習中未達標", inProgressUsers, totalUsers));
		distributions.add(createDistributionVO("未開始", notStartedUsers, totalUsers));
		return distributions;
	}

	/**
	 * 分頁查詢 企業 年度學員學習明細 (ESG 審計用)<br>
	 * 一列 = 一筆修課紀錄 (學員 × 課程)
	 *
	 * @param pageInfo    分頁對象
	 * @param operator    操作者 (企業管理者/HR)
	 * @param year        統計年度，非必要，null 則取當年
	 * @param isCompleted 是否完課取得證書；null=全部修課紀錄, true=已完課, false=未完成
	 * @param queryText   查詢條件，比對姓名與課程名稱
	 * @return
	 */
	public IPage<EsgLearningDetailVO> findAnnualLearningDetailPage(Page<CourseEnrollment> pageInfo, SysUserVO operator,
			Integer year, Boolean isCompleted, String queryText) {

		List<EsgLearningDetailVO> rows = buildLearningDetailRows(operator, resolveYear(year), isCompleted, queryText);

		Page<EsgLearningDetailVO> resultPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), rows.size());
		resultPage.setRecords(subListForPage(rows, pageInfo.getCurrent(), pageInfo.getSize()));
		return resultPage;
	}

	/**
	 * 下載 企業 年度學員學習明細 Excel (ESG 審計用)<br>
	 * 不分頁，輸出符合條件的全部明細
	 *
	 * @param response    HTTP響應，用於輸出Excel檔案
	 * @param operator    操作者 (企業管理者/HR)
	 * @param year        統計年度，非必要，null 則取當年
	 * @param isCompleted 是否完課取得證書；null=全部修課紀錄, true=已完課, false=未完成
	 * @param queryText   查詢條件，比對姓名與課程名稱
	 */
	public void downloadAnnualLearningDetailExcel(HttpServletResponse response, SysUserVO operator, Integer year,
			Boolean isCompleted, String queryText) throws IOException {

		int targetYear = resolveYear(year);

		// 1.設置Excel 檔案資訊
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		String fileName = URLEncoder.encode(targetYear + "年度ESG審計學員學習明細", StandardCharsets.UTF_8).replaceAll("\\+",
				"%20");
		response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");

		// 2.取得符合條件的全部明細 (與分頁查詢共用同一組裝方法，確保畫面與檔案內容一致)
		List<EsgLearningDetailVO> rows = buildLearningDetailRows(operator, targetYear, isCompleted, queryText);

		// 3.轉換為 Excel 資料列
		List<EsgLearningDetailExcel> excelData = rows.stream().map(row -> {
			EsgLearningDetailExcel excelRow = new EsgLearningDetailExcel();
			excelRow.setUserName(row.getUserName());
			excelRow.setDepartmentName(row.getDepartmentName());
			excelRow.setCourseName(row.getCourseName());
			excelRow.setStatus(row.getStatus() != null ? row.getStatus().getLabelZh() : null);
			excelRow.setCompletedAt(row.getCompletedAt());
			excelRow.setAccumulatedHours(row.getAccumulatedHours());
			excelRow.setRequiredHours(row.getRequiredHours());
			excelRow.setIsCompleted(CommonStatusEnum.YES.equals(row.getIsCompleted()) ? "是" : "否");
			excelRow.setIsActive(CommonStatusEnum.YES.equals(row.getIsActive()) ? "是" : "否");
			return excelRow;
		}).toList();

		// 4.輸出成Excel
		EasyExcel.write(response.getOutputStream(), EsgLearningDetailExcel.class)
				.sheet(targetYear + "年度學習明細")
				.doWrite(excelData);
	}

	/**
	 * 組裝 年度學員學習明細 (供分頁查詢 & Excel 匯出共用)<br>
	 * 為保留完整佐證軌跡，此處<b>不</b>過濾已停用員工：當年確實受過訓的紀錄即使人員已離職仍應留存，<br>
	 * 改以 isActive 欄位標示；因此本明細的筆數會 ≥ KPI/圖表 API 的統計基數
	 *
	 * @param operator    操作者
	 * @param targetYear  統計年度
	 * @param isCompleted 是否完課取得證書；null=全部, true=已完課, false=未完成
	 * @param queryText   查詢條件，比對姓名與課程名稱
	 */
	private List<EsgLearningDetailVO> buildLearningDetailRows(SysUserVO operator, int targetYear, Boolean isCompleted,
			String queryText) {

		// 1.取得該年度報名資料，並依 tab 條件篩選
		List<CourseEnrollment> yearEnrollments = findYearEnrollments(operator.getCompanyId(), targetYear).stream()
				.filter(enrollment -> matchCompletedFilter(enrollment, isCompleted))
				.toList();

		if (yearEnrollments.isEmpty()) {
			return Collections.emptyList();
		}

		// 2.補上員工資料 (以報名紀錄反查，確保已停用/已轉調的員工也查得到姓名)
		Set<Long> userIds = yearEnrollments.stream().map(CourseEnrollment::getSysUserId).collect(Collectors.toSet());
		Map<Long, SysUser> userMap = sysUserService.listByIds(userIds)
				.stream()
				.collect(Collectors.toMap(SysUser::getSysUserId, Function.identity()));

		// 3.補上部門名稱 Map
		Map<Long, Department> deptMap = departmentService.findByCompany(operator.getCompanyId())
				.stream()
				.collect(Collectors.toMap(Department::getDepartmentId, Function.identity()));

		// 4.補上課程名稱 Map
		Set<Long> courseIds = yearEnrollments.stream().map(CourseEnrollment::getCourseId).collect(Collectors.toSet());
		Map<Long, Course> courseMap = courseService.findByIds(courseIds)
				.stream()
				.collect(Collectors.toMap(Course::getCourseId, Function.identity()));

		// 5.組裝每一列，並套用文字查詢條件
		return yearEnrollments.stream().map(enrollment -> {

			SysUser user = userMap.get(enrollment.getSysUserId());
			Course course = courseMap.get(enrollment.getCourseId());

			// 部門優先取員工現況部門，查不到才退回報名當下的部門快照
			Long departmentId = (user != null && user.getDepartmentId() != null) ? user.getDepartmentId()
					: enrollment.getDepartmentId();
			Department department = departmentId != null ? deptMap.get(departmentId) : null;

			EsgLearningDetailVO vo = new EsgLearningDetailVO();
			vo.setCourseEnrollmentId(enrollment.getCourseEnrollmentId());
			vo.setSysUserId(enrollment.getSysUserId());
			vo.setUserName(user != null ? user.getRealName() : null);
			vo.setDepartmentName(department != null ? department.getName() : null);
			vo.setCourseName(course != null ? course.getTitle() : null);
			vo.setStatus(enrollment.getStatus());
			vo.setCompletedAt(enrollment.getCompletedAt());
			vo.setAccumulatedHours(round1(secondsToHours(enrollment.getAccumulatedSeconds())));
			vo.setRequiredHours(round1(secondsToHours(enrollment.getRequiredSeconds())));
			vo.setIsCompleted(CourseStatusEnum.COMPLETED.equals(enrollment.getStatus()) ? CommonStatusEnum.YES
					: CommonStatusEnum.NO);
			vo.setIsActive(user != null && CommonStatusEnum.YES.equals(user.getIsActive()) ? CommonStatusEnum.YES
					: CommonStatusEnum.NO);
			return vo;

		})
				.filter(vo -> matchQueryText(vo, queryText))
				// 固定排序，確保分頁與匯出結果可重現 (審計要求)
				.sorted(Comparator.comparing(EsgLearningDetailVO::getDepartmentName,
						Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(EsgLearningDetailVO::getUserName,
								Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(EsgLearningDetailVO::getCourseName,
								Comparator.nullsLast(Comparator.naturalOrder())))
				.toList();
	}

	/**
	 * 輔助方法：tab 篩選<br>
	 * null=全部修課紀錄, true=已完課取得證書, false=未完成 (completed 以外的狀態皆算)<br>
	 * 「已完課」與「未完成」兩者互斥且加總等於「全部」，方便 HR 核對筆數
	 */
	private boolean matchCompletedFilter(CourseEnrollment enrollment, Boolean isCompleted) {
		if (isCompleted == null) {
			return true;
		}
		boolean completed = CourseStatusEnum.COMPLETED.equals(enrollment.getStatus());
		return isCompleted == completed;
	}

	/**
	 * 輔助方法：文字查詢，比對姓名與課程名稱
	 */
	private boolean matchQueryText(EsgLearningDetailVO vo, String queryText) {
		if (StringUtils.isBlank(queryText)) {
			return true;
		}
		return StringUtils.containsIgnoreCase(vo.getUserName(), queryText)
				|| StringUtils.containsIgnoreCase(vo.getCourseName(), queryText);
	}

	/**
	 * 輔助方法：記憶體分頁切片 (頁碼超出範圍時回傳空列表)
	 */
	private <T> List<T> subListForPage(List<T> rows, long current, long size) {
		if (size <= 0 || current <= 0) {
			return Collections.emptyList();
		}
		long fromIndex = (current - 1) * size;
		if (fromIndex >= rows.size()) {
			return Collections.emptyList();
		}
		int from = (int) fromIndex;
		int to = (int) Math.min(fromIndex + size, rows.size());
		return rows.subList(from, to);
	}

	/**
	 * 輔助方法：秒數換算為時數 (null 視為 0，不做四捨五入)
	 */
	private double secondsToHours(Integer seconds) {
		return (seconds != null ? seconds : 0) / 3600.0;
	}

	/**
	 * 輔助方法：決定統計年度，未指定則取當年
	 */
	private int resolveYear(Integer year) {
		return (year != null) ? year : LocalDate.now().getYear();
	}

	/**
	 * 輔助方法：取得全公司「當前活躍」的員工<br>
	 * findByCompany 內部已排除同 parentId 的企業管理者，故此處人數即為企業員工數
	 */
	private List<SysUser> findActiveEmployees(SysUserVO operator) {
		return sysUserService.findByCompany(operator.getParentId(), operator.getCompanyId(), null, null)
				.stream()
				.filter(user -> CommonStatusEnum.YES.equals(user.getIsActive()))
				.toList();
	}

	/**
	 * 輔助方法：取得該公司於指定年度報名的所有課程報名資料
	 */
	private List<CourseEnrollment> findYearEnrollments(Long companyId, int targetYear) {
		return courseEnrollmentService.findCompanyLearningProgress(companyId, null, null)
				.stream()
				.filter(enrollment -> enrollment.getEnrolledAt() != null
						&& enrollment.getEnrolledAt().getYear() == targetYear)
				.toList();
	}

	/**
	 * 輔助方法：判定該員工是否完訓 (被分配的課全部 completed，未被分配課程不算完訓)
	 */
	private boolean isFullyCompleted(List<CourseEnrollment> enrollments) {
		return !enrollments.isEmpty()
				&& enrollments.stream().allMatch(e -> CourseStatusEnum.COMPLETED.equals(e.getStatus()));
	}

	/**
	 * 輔助方法：學習秒數加總並換算為時數 (不做四捨五入，供後續相除使用)
	 */
	private double sumHours(List<CourseEnrollment> enrollments) {
		int totalSeconds = enrollments.stream()
				.mapToInt(enrollment -> enrollment.getAccumulatedSeconds() != null ? enrollment.getAccumulatedSeconds()
						: 0)
				.sum();
		return totalSeconds / 3600.0;
	}

	/**
	 * 輔助方法：封裝比例與計算四捨五入百分比
	 */
	private LearningStatusDistributionVO createDistributionVO(String statusName, int count, int total) {
		LearningStatusDistributionVO distVO = new LearningStatusDistributionVO();
		distVO.setStatusName(statusName);
		distVO.setCount(count);
		distVO.setPercentage(round1(total > 0 ? (double) count / total * 100 : 0.0));
		return distVO;
	}

	/**
	 * 輔助方法：四捨五入到小數點後 1 位
	 */
	private double round1(double value) {
		return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
	}

}
