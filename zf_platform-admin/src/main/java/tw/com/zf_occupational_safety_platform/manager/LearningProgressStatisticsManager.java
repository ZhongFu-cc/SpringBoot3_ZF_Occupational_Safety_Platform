package tw.com.zf_occupational_safety_platform.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.merge.OnceAbsoluteMergeStrategy;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.LearningProgressStatisticsConvert;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.VO.DepartmentRankingVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressChartVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressKpiVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressTableVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningStatusDistributionVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.pojo.excel.CompanyLearningStatisticsDetailExcel;
import tw.com.zf_occupational_safety_platform.pojo.excel.CompanyLearningStatisticsExcel;
import tw.com.zf_occupational_safety_platform.service.CourseEnrollmentService;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * 課程總體進度 - 管理層
 */
@Component
@RequiredArgsConstructor
public class LearningProgressStatisticsManager {

	private final CourseEnrollmentService courseEnrollmentService;
	private final DepartmentService departmentService;
	private final SysUserService sysUserService;
	private final CourseService courseService;
	private final LearningProgressStatisticsConvert learningProgressStatisticsConvert;

	/**
	 * 獲取企業內 課程總體進度KPI
	 * 
	 * @param departmentId
	 * @param courseId
	 * @return
	 */
	public LearningProgressKpiVO getCourseProgressKpi(SysUserVO operator, Long departmentId, Long courseId) {

		LearningProgressKpiVO vo = new LearningProgressKpiVO();
		// 1.查找企業內學習進度
		List<CourseEnrollment> companyLearningProgress = courseEnrollmentService
				.findCompanyLearningProgress(operator.getCompanyId(), departmentId, courseId);

		// 如果目前尚未有資料則返回默認資料
		if (companyLearningProgress == null || companyLearningProgress.isEmpty()) {
			vo.setCompletionRate(0.0);
			vo.setExpiredCount(0);
			vo.setTotalTrainingHours(0.0);
			vo.setUrgentCount(0);
			return vo;
		}

		// 2.建立一個以 userId 為key , List<CourseEntollment> 為 value 的 Map對象
		Map<Long, List<CourseEnrollment>> userProgressMap = companyLearningProgress.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		int totalUsers = userProgressMap.size(); // 總學員人數

		/*
		 * 核心指標 1：合規完課率 (按人頭：必須6門課皆 completed 才算 100%)
		 */
		long fullyCompletedUsers = userProgressMap.values().stream().filter(enrollments -> {
			if (courseId != null) {
				// 單課查詢：該課 status 為 completed 即可
				return enrollments.stream().anyMatch(e -> e.getStatus() == CourseStatusEnum.COMPLETED);
			} else {
				// 全局查詢：已完成的課程數量 == HR 分發給他的總課程數量
				long completedCount = enrollments.stream()
						.filter(e -> e.getStatus() == CourseStatusEnum.COMPLETED)
						.count();
				int assignedCount = enrollments.size(); // HR 幫他分發的總課數

				return completedCount == assignedCount;
			}
		}).count();

		double completionRate = totalUsers > 0 ? (double) fullyCompletedUsers / totalUsers * 100 : 0.0;
		// 格式化為小數點後 1 位
		double roundedRate = BigDecimal.valueOf(completionRate).setScale(1, RoundingMode.HALF_UP).doubleValue();
		vo.setCompletionRate(roundedRate); // VO 需新增此欄位

		/**
		 * 核心指標 2：課程證照已過期人數 (不重複人頭)
		 */
		long expiredUsersCount = userProgressMap.values()
				.stream()
				.filter(enrollments -> enrollments.stream()
						.anyMatch(e -> CourseStatusEnum.EXPIRED.equals(e.getStatus())))
				.count();
		vo.setExpiredCount((int) expiredUsersCount);

		/**
		 * 修習總時數 (學習秒數加總，轉換成時數，四捨五入到小數點 1 位)
		 */
		Double totalHours = companyLearningProgress.stream()
				.mapToInt(enrollment -> enrollment.getAccumulatedSeconds() != null ? enrollment.getAccumulatedSeconds()
						: 0)
				.sum() / 3600.0;

		Double roundedHours = BigDecimal.valueOf(totalHours).setScale(1, RoundingMode.HALF_UP).doubleValue();

		// 設定修習總時數
		vo.setTotalTrainingHours(roundedHours);

		/**
		 * 核心指標 4：待催促人數 (有任一門課狀態為 not_started 或 in_progress 的不重複人數)
		 */
		long urgentUsersCount = userProgressMap.values()
				.stream()
				.filter(enrollments -> enrollments.stream()
						.anyMatch(e -> CourseStatusEnum.NOT_STARTED.equals(e.getStatus())
								|| CourseStatusEnum.IN_PROGRESS.equals(e.getStatus())))
				.count();
		vo.setUrgentCount((int) urgentUsersCount); // VO 需新增此欄位

		return vo;

	}

	/**
	 * 獲取企業內 各部門課程完課率 & 學習狀態分佈 圖表
	 * 
	 * @param operator
	 * @param departmentId
	 * @param courseId
	 */
	public LearningProgressChartVO getCourseProgressChart(SysUserVO operator, Long departmentId, Long courseId) {
		LearningProgressChartVO vo = new LearningProgressChartVO();

		// 1. 查找企業內學習進度 (包含所有課程或指定課程)
		List<CourseEnrollment> companyLearningProgress = courseEnrollmentService
				.findCompanyLearningProgress(operator.getCompanyId(), departmentId, courseId);

		// 若無資料，返回空列表避免前端報錯
		if (companyLearningProgress == null || companyLearningProgress.isEmpty()) {
			vo.setDepartmentRankings(Collections.emptyList());
			vo.setLearningStatusDistribution(Collections.emptyList());
			return vo;
		}

		// 拿到公司內所有部門 Map
		List<Department> departments = departmentService.findByCompany(operator.getCompanyId());
		Map<Long, Department> deptMap = departments.stream()
				.collect(Collectors.toMap(Department::getDepartmentId, Function.identity()));

		// 部門完課排行計算 (一次分組，效率最高)
		// 1. 直接按「員工 ID」進行全局分組 (一次性將所有人頭歸類)
		Map<Long, List<CourseEnrollment>> userProgressMap = companyLearningProgress.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		// 2. 建立兩個臨時 Map，用來累計每個部門的「總人數」與「合格人數」
		// Key 是 departmentId
		Map<Long, Integer> deptTotalUsersMap = new HashMap<>();
		Map<Long, Integer> deptCompliantUsersMap = new HashMap<>();

		// 3. 遍歷一次「員工 Map」，同時計算「員工是否合格」與「部門累計」
		for (Map.Entry<Long, List<CourseEnrollment>> userEntry : userProgressMap.entrySet()) {
			List<CourseEnrollment> userEnrollments = userEntry.getValue();
			if (userEnrollments.isEmpty()) {
				continue;
			}

			// 因為同一個學員的課程都在同一個部門，直接取第一筆的部門資訊
			CourseEnrollment firstEnrollment = userEnrollments.get(0);
			Long deptId = firstEnrollment.getDepartmentId();

			// 判斷該學員是否合格
			boolean isUserCompliant;
			if (courseId != null) {
				// 單課查詢：該課 status 為 COMPLETED 即可
				isUserCompliant = userEnrollments.stream().anyMatch(e -> e.getStatus() == CourseStatusEnum.COMPLETED);
			} else {
				// 全局查詢：已完成數 == 分發總數
				long completedCount = userEnrollments.stream()
						.filter(e -> e.getStatus() == CourseStatusEnum.COMPLETED)
						.count();
				isUserCompliant = (completedCount == userEnrollments.size());
			}

			// 累計部門總人數
			deptTotalUsersMap.put(deptId, deptTotalUsersMap.getOrDefault(deptId, 0) + 1);

			// 如果合格，累計部門合格人數
			if (isUserCompliant) {
				deptCompliantUsersMap.put(deptId, deptCompliantUsersMap.getOrDefault(deptId, 0) + 1);
			}
		}

		// 4. 組裝 DepartmentRankingVO
		List<DepartmentRankingVO> departmentRankings = deptTotalUsersMap.entrySet().stream().map(entry -> {
			Long deptId = entry.getKey();
			int totalUsersInDept = entry.getValue();
			int compliantUsersCount = deptCompliantUsersMap.getOrDefault(deptId, 0);

			// 計算完課率
			double rate = totalUsersInDept > 0 ? (double) compliantUsersCount / totalUsersInDept * 100 : 0.0;
			double roundedRate = BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP).doubleValue();

			// 取得部門名稱 (優先從 database 查出的 deptMap 取，取不到才用 enrollment 的)
			Department deptObj = deptMap.get(deptId);

			DepartmentRankingVO rankingVO = new DepartmentRankingVO();
			rankingVO.setDepartmentId(deptId);
			rankingVO.setDepartmentName(deptObj.getName());
			rankingVO.setCompletionRate(roundedRate);
			return rankingVO;
		}).sorted(Comparator.comparing(DepartmentRankingVO::getCompletionRate).reversed()).collect(Collectors.toList());

		vo.setDepartmentRankings(departmentRankings);

		/**
		 * 計算【學習狀態比例】(統計「員工學習狀態分佈」而非課次分佈)
		 */

		// 將所有數據按員工 ID 分組
		Map<Long, List<CourseEnrollment>> allUserMap = companyLearningProgress.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		int totalUsers = allUserMap.size(); // 全公司符合篩選條件的總人數
		int completedUsers = 0; // 全部課程都完課的人 (合格)
		int inProgressUsers = 0; // 只要有課在看/已過期、但還沒全部看完的人 (學習中)
		int notStartedUsers = 0; // 所有被分發的課都還沒開始的人 (未開始)

		for (List<CourseEnrollment> userEnrollments : allUserMap.values()) {
			long completedCount = userEnrollments.stream()
					.filter(e -> e.getStatus() == CourseStatusEnum.COMPLETED)
					.count();
			long notStartedCount = userEnrollments.stream()
					.filter(e -> e.getStatus() == CourseStatusEnum.NOT_STARTED)
					.count();
			int totalAssigned = userEnrollments.size();

			if (completedCount == totalAssigned) {
				completedUsers++;
			} else if (notStartedCount == totalAssigned) {
				notStartedUsers++;
			} else {
				inProgressUsers++;
			}
		}

		List<LearningStatusDistributionVO> statusDistributions = new ArrayList<>();

		// 依序手動包裝，提供給 HR 最直觀的三種「人頭進度」狀態
		statusDistributions
				.add(createDistributionVO(CourseStatusEnum.COMPLETED.getLabelZh(), completedUsers, totalUsers));
		statusDistributions
				.add(createDistributionVO(CourseStatusEnum.IN_PROGRESS.getLabelZh(), inProgressUsers, totalUsers));
		statusDistributions
				.add(createDistributionVO(CourseStatusEnum.NOT_STARTED.getLabelZh(), notStartedUsers, totalUsers));

		vo.setLearningStatusDistribution(statusDistributions);

		return vo;
	}

	/**
	 * 輔助方法：封裝比例與計算四捨五入百分比
	 */
	private LearningStatusDistributionVO createDistributionVO(String statusName, int count, int total) {
		double percentage = total > 0 ? (double) count / total * 100 : 0.0;
		double roundedPercentage = BigDecimal.valueOf(percentage).setScale(1, RoundingMode.HALF_UP).doubleValue();

		LearningStatusDistributionVO distVO = new LearningStatusDistributionVO();
		distVO.setStatusName(statusName);
		distVO.setCount(count);
		distVO.setPercentage(roundedPercentage);
		return distVO;
	}

	/**
	 * 獲取企業內 上課進度詳細 table
	 * 
	 * @param pageInfo     分頁對象
	 * @param operator     操作者
	 * @param departmentId 部門ID
	 * @param courseId     課程ID
	 * @param queryText    查詢條件
	 */
	public IPage<LearningProgressTableVO> getCourseProgressTable(Page<SysUser> pageInfo, SysUserVO operator,
			Long departmentId, Long courseId, String queryText) {

		// 先拿到當頁的用戶
		IPage<SysUser> userPage = sysUserService.findByCompany(pageInfo, operator.getParentId(),
				operator.getCompanyId(), departmentId, queryText);

		List<SysUser> users = userPage.getRecords();
		// 若該頁沒有員工，直接返回空分頁
		if (users == null || users.isEmpty()) {
			return new Page<LearningProgressTableVO>(pageInfo.getCurrent(), pageInfo.getSize());
		}

		// 拿到公司內所有部門 Map，用於補上部門名稱
		List<Department> departments = departmentService.findByCompany(operator.getCompanyId());
		Map<Long, Department> deptMap = departments.stream()
				.collect(Collectors.toMap(Department::getDepartmentId, Function.identity()));

		// 拿到企業內（依部門/課程篩選）的學習進度，再依 sysUserId 分組
		List<CourseEnrollment> companyLearningProgress = courseEnrollmentService
				.findCompanyLearningProgress(operator.getCompanyId(), departmentId, courseId);
		Map<Long, List<CourseEnrollment>> userProgressMap = companyLearningProgress.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		// 組裝當頁每位員工的課程進度資料
		List<LearningProgressTableVO> records = buildProgressTableRows(users, deptMap, userProgressMap);

		Page<LearningProgressTableVO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(),
				userPage.getTotal());
		resultPage.setRecords(records);
		return resultPage;

	}

	/**
	 * 依「員工清單 / 部門Map / 員工課程進度Map」組裝每位員工的課程進度列 (供 table 查詢 & Excel 匯出共用)
	 */
	private List<LearningProgressTableVO> buildProgressTableRows(List<SysUser> users, Map<Long, Department> deptMap,
			Map<Long, List<CourseEnrollment>> userProgressMap) {

		return users.stream().map(user -> {
			List<CourseEnrollment> enrollments = userProgressMap.getOrDefault(user.getSysUserId(),
					Collections.emptyList());

			int totalCourses = enrollments.size();
			long completedCount = enrollments.stream().filter(e -> e.getStatus() == CourseStatusEnum.COMPLETED)
					.count();
			long notStartedCount = enrollments.stream().filter(e -> e.getStatus() == CourseStatusEnum.NOT_STARTED)
					.count();

			CourseStatusEnum overallStatus;
			if (totalCourses == 0 || notStartedCount == totalCourses) {
				overallStatus = CourseStatusEnum.NOT_STARTED;
			} else if (completedCount == totalCourses) {
				overallStatus = CourseStatusEnum.COMPLETED;
			} else {
				overallStatus = CourseStatusEnum.IN_PROGRESS;
			}

			Department department = deptMap.get(user.getDepartmentId());

			LearningProgressTableVO vo = new LearningProgressTableVO();
			vo.setSysUserId(user.getSysUserId());
			vo.setUserName(user.getRealName());
			vo.setDepatmentId(user.getDepartmentId());
			vo.setDepartmentName(department != null ? department.getName() : null);
			vo.setTotalCourses(totalCourses);
			vo.setCompletedCourses((int) completedCount);
			vo.setOverallStatus(overallStatus.name());
			return vo;
		}).collect(Collectors.toList());
	}

	/**
	 * 下載企業員工上課結果統計 Excel<br>
	 * 內含兩個分頁：「整體統計」為每位員工的彙總數據，「課程明細」為每位員工每門課的詳細進度 (以合併儲存格呈現同一員工的多門課程)
	 *
	 * @param response     HTTP響應，用於輸出Excel檔案
	 * @param operator     操作者
	 * @param departmentId 部門ID
	 * @param courseId     課程ID
	 * @param queryText    用戶查詢條件
	 */
	public void downloadCourseProgressExcel(HttpServletResponse response, SysUserVO operator, Long departmentId,
			Long courseId, String queryText) throws IOException {

		// 1.設置Excel 檔案資訊
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setCharacterEncoding("utf-8");
		String fileName = URLEncoder.encode("企業員工上課結果統計", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
		response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");

		// 2.取得符合條件的員工清單 (不分頁，取得該公司/部門下所有員工)
		List<SysUser> users = sysUserService.findByCompany(operator.getParentId(), operator.getCompanyId(),
				departmentId, queryText);

		// 3.取得公司內所有部門 Map，用於補上部門名稱
		Map<Long, Department> deptMap = departmentService.findByCompany(operator.getCompanyId()).stream()
				.collect(Collectors.toMap(Department::getDepartmentId, Function.identity()));

		// 4.取得企業內（依部門/課程篩選）的學習進度，再依 sysUserId 分組
		List<CourseEnrollment> companyLearningProgress = courseEnrollmentService
				.findCompanyLearningProgress(operator.getCompanyId(), departmentId, courseId);
		Map<Long, List<CourseEnrollment>> userProgressMap = companyLearningProgress.stream()
				.collect(Collectors.groupingBy(CourseEnrollment::getSysUserId));

		// 5.取得課程名稱 Map，用於補上課程明細的課程名稱
		Set<Long> courseIds = companyLearningProgress.stream().map(CourseEnrollment::getCourseId)
				.collect(Collectors.toSet());
		Map<Long, Course> courseMap = courseService.findByIds(courseIds).stream()
				.collect(Collectors.toMap(Course::getCourseId, Function.identity()));

		// 6.組裝「整體統計」分頁資料
		List<CompanyLearningStatisticsExcel> summaryData = buildProgressTableRows(users, deptMap, userProgressMap)
				.stream()
				.map(learningProgressStatisticsConvert::toSummaryExcel)
				.toList();

		// 7.組裝「課程明細」分頁資料，並計算合併儲存格範圍 (同一員工的多門課程合併姓名/部門欄位)
		List<CompanyLearningStatisticsDetailExcel> detailData = new ArrayList<>();
		List<OnceAbsoluteMergeStrategy> detailMergeStrategies = new ArrayList<>();
		buildDetailRows(users, deptMap, userProgressMap, courseMap, detailData, detailMergeStrategies);

		// 8.輸出成Excel (兩個分頁)
		try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
			WriteSheet summarySheet = EasyExcel.writerSheet(0, "整體統計").head(CompanyLearningStatisticsExcel.class)
					.build();
			excelWriter.write(summaryData, summarySheet);

			var detailSheetBuilder = EasyExcel.writerSheet(1, "課程明細").head(CompanyLearningStatisticsDetailExcel.class);
			detailMergeStrategies.forEach(detailSheetBuilder::registerWriteHandler);
			excelWriter.write(detailData, detailSheetBuilder.build());
		}
	}

	/**
	 * 組裝「課程明細」分頁：每位員工的每一門課一列，並計算合併儲存格範圍 (姓名/部門欄位)，讓同一員工的多門課程合併呈現
	 *
	 * @param users            員工清單
	 * @param deptMap          部門 Map
	 * @param userProgressMap  員工課程進度 Map (key: sysUserId)
	 * @param courseMap        課程 Map (key: courseId)，用於補上課程名稱
	 * @param detailData       [輸出] 組裝完成的課程明細列
	 * @param mergeStrategies  [輸出] 合併儲存格策略 (姓名欄與部門欄)
	 */
	private void buildDetailRows(List<SysUser> users, Map<Long, Department> deptMap,
			Map<Long, List<CourseEnrollment>> userProgressMap, Map<Long, Course> courseMap,
			List<CompanyLearningStatisticsDetailExcel> detailData, List<OnceAbsoluteMergeStrategy> mergeStrategies) {

		// EasyExcel 預設以第0列為表頭，資料列從第1列(index)開始
		int rowIndex = 1;

		for (SysUser user : users) {
			List<CourseEnrollment> enrollments = userProgressMap.getOrDefault(user.getSysUserId(),
					Collections.emptyList());
			Department department = deptMap.get(user.getDepartmentId());
			String departmentName = department != null ? department.getName() : null;

			if (enrollments.isEmpty()) {
				CompanyLearningStatisticsDetailExcel row = new CompanyLearningStatisticsDetailExcel();
				row.setUserName(user.getRealName());
				row.setDepartmentName(departmentName);
				row.setCourseName("尚未分派課程");
				detailData.add(row);
				rowIndex++;
				continue;
			}

			int startRow = rowIndex;
			for (CourseEnrollment enrollment : enrollments) {
				Course course = courseMap.get(enrollment.getCourseId());
				CompanyLearningStatisticsDetailExcel row = learningProgressStatisticsConvert
						.toDetailExcel(user.getRealName(), departmentName, course, enrollment);
				detailData.add(row);
				rowIndex++;
			}

			// 該員工佔多列時，合併姓名(欄0)/部門(欄1)儲存格
			int endRow = rowIndex - 1;
			if (endRow > startRow) {
				mergeStrategies.add(new OnceAbsoluteMergeStrategy(startRow, endRow, 0, 0));
				mergeStrategies.add(new OnceAbsoluteMergeStrategy(startRow, endRow, 1, 1));
			}
		}
	}

}
