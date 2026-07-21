package tw.com.zf_occupational_safety_platform.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.VO.LearningProgressTableVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.excel.CompanyLearningStatisticsDetailExcel;
import tw.com.zf_occupational_safety_platform.pojo.excel.CompanyLearningStatisticsExcel;

@Mapper(componentModel = "spring")
public interface LearningProgressStatisticsConvert {

	/**
	 * 員工課程進度VO 轉換為「整體統計」Excel列
	 */
	@Mapping(target = "completionRate", expression = "java(calculateCompletionRate(vo.getCompletedCourses(), vo.getTotalCourses()))")
	@Mapping(target = "overallStatus", source = "overallStatus", qualifiedByName = "convertOverallStatusLabel")
	CompanyLearningStatisticsExcel toSummaryExcel(LearningProgressTableVO vo);

	/**
	 * 結合 員工姓名 / 部門名稱 / 課程 / 報名紀錄 轉換為「課程明細」Excel列
	 */
	@Mapping(target = "courseName", source = "course.title")
	@Mapping(target = "status", source = "enrollment.status", qualifiedByName = "convertCourseStatusLabel")
	@Mapping(target = "completedChapters", source = "enrollment.completedChapters")
	@Mapping(target = "totalChapters", source = "enrollment.totalChapters")
	@Mapping(target = "accumulatedHours", source = "enrollment.accumulatedSeconds", qualifiedByName = "secondsToHours")
	@Mapping(target = "requiredHours", source = "enrollment.requiredSeconds", qualifiedByName = "secondsToHours")
	@Mapping(target = "completedAt", source = "enrollment.completedAt")
	CompanyLearningStatisticsDetailExcel toDetailExcel(String userName, String departmentName, Course course,
			CourseEnrollment enrollment);

	/**
	 * LearningProgressTableVO.overallStatus 為 CourseStatusEnum 的 name() 字串，轉換為中文標籤
	 */
	@Named("convertOverallStatusLabel")
	default String convertOverallStatusLabel(String overallStatus) {
		return overallStatus != null ? CourseStatusEnum.valueOf(overallStatus).getLabelZh() : null;
	}

	@Named("convertCourseStatusLabel")
	default String convertCourseStatusLabel(CourseStatusEnum status) {
		return status != null ? status.getLabelZh() : null;
	}

	/**
	 * 秒數轉換為時數，四捨五入到小數點後1位
	 */
	@Named("secondsToHours")
	default Double secondsToHours(Integer seconds) {
		double hours = (seconds != null ? seconds : 0) / 3600.0;
		return BigDecimal.valueOf(hours).setScale(1, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 計算完課率(%)，四捨五入到小數點後1位
	 */
	default Double calculateCompletionRate(Integer completedCourses, Integer totalCourses) {
		int completed = completedCourses != null ? completedCourses : 0;
		int total = totalCourses != null ? totalCourses : 0;
		double rate = total > 0 ? (double) completed / total * 100 : 0.0;
		return BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP).doubleValue();
	}
}
