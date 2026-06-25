package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.enums.CourseStatusEnum;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CompanyCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseVO;
import tw.com.zf_occupational_safety_platform.pojo.VO.JobCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeStudyHistoryExcel;

@Mapper(componentModel = "spring")
public interface CourseConvert {

	Course addDTOToEntity(AddCourseDTO addCourseDTO);

	Course putDTOToEntity(PutCourseDTO putCourseDTO);

	CourseVO entityToVO(Course course);

	CompanyCourseVO entityToCompanyCourseVO(Course course);

	JobCourseVO entityToJobCourseVO(Course course);

	/**
	 * 結合 Course 與 CourseEnrollment 轉換為 Excel 物件
	 */
	// 1. 來自 Course 的欄位
	@Mapping(target = "courseName", source = "course.title")
	// 如果 Excel 的 courseCategoryName 需要從分類名稱拿，但 Course 只有 id，可先忽略或像這樣自訂（這裡先對應名稱，通常會需要額外查，若沒有可刪除此行）
	// @Mapping(target = "courseCategoryName", source = "course.courseCategoryId", qualifiedByName = "你的分類查詢邏輯") 

	// 2. 來自 CourseEnrollment 的欄位 (同名欄位若無衝突 MapStruct 會自動對應，但多參數時明確指定 source 較安全)
	@Mapping(target = "courseEnrollmentId", source = "enrollment.courseEnrollmentId")
	@Mapping(target = "totalChapters", source = "enrollment.totalChapters")
	@Mapping(target = "completedChapters", source = "enrollment.completedChapters")
	@Mapping(target = "requiredSeconds", source = "enrollment.requiredSeconds")
	@Mapping(target = "accumulatedSeconds", source = "enrollment.accumulatedSeconds")
	@Mapping(target = "enrolledAt", source = "enrollment.enrolledAt")
	@Mapping(target = "startedAt", source = "enrollment.startedAt")
	@Mapping(target = "completedAt", source = "enrollment.completedAt")
	@Mapping(target = "expiredAt", source = "enrollment.expiredAt")

	// 3. 需要透過 @Named 自訂轉換的列舉欄位
	@Mapping(target = "status", source = "enrollment.status", qualifiedByName = "convertCourseStatus")
	@Mapping(target = "isChaptersDone", source = "enrollment.isChaptersDone", qualifiedByName = "convertCommonStatus")
	@Mapping(target = "isMinutesMet", source = "enrollment.isMinutesMet", qualifiedByName = "convertCommonStatus")
	EmployeeStudyHistoryExcel toEmployeeStudyHistoryExcel(Course course, CourseEnrollment enrollment);

	/**
	 * 將 CourseStatusEnum 轉換為 Excel 顯示的中文標籤
	 */
	@Named("convertCourseStatus")
	default String convertCourseStatus(CourseStatusEnum status) {
		return status.getLabelZh();
	}

	/**
	 * 將 CommonStatusEnum (0=否, 1=是) 轉換為 Excel 顯示的 "是" / "否"
	 */
	@Named("convertCommonStatus")
	default String convertCommonStatus(CommonStatusEnum commonStatus) {
		// 假設你的 CommonStatusEnum 內建定義是 YES/NO 或 1/0
		return commonStatus == CommonStatusEnum.YES ? "是" : "否";
	}
}
