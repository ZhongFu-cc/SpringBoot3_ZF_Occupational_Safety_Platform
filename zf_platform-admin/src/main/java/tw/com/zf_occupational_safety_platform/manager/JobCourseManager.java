package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.pojo.VO.JobCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;
import tw.com.zf_occupational_safety_platform.service.CourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeCourseService;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;

/**
 * 作業類別 x 課程類別 關聯 - 管理層
 */
@Component
@RequiredArgsConstructor
public class JobCourseManager {

	private final JobTypeService jobTypeService;
	private final CourseService courseService;
	private final JobTypeCourseService jobTypeCourseService;
	private final CourseConvert courseConvert;

	/**
	 * 根據 jobType 查詢關聯的課程類別
	 * 
	 * @param jobTypeId
	 * @param pageInfo
	 * @param queryText
	 * @return
	 */
	public IPage<JobCourseVO> findJobCoursePageByJobType(Page<JobTypeCourse> pageInfo, Long jobTypeId,
			String queryText) {

		// 1.查詢符合模糊查詢 的 課程對象，並拿到map映射
		Map<Long, Course> courseIdMap = courseService.findCourseIdMapByQuery(queryText);
		// 2.抽取keys
		List<Long> courseIds = courseIdMap.keySet().stream().toList();

		// 3.拿到關聯的page對象
		IPage<JobTypeCourse> jobCoursePage = jobTypeCourseService.findPageByTypeIdAndCourseIds(pageInfo, jobTypeId,
				courseIds);

		// 4.課程資料與關聯資料做整合
		List<JobCourseVO> voList = jobCoursePage.getRecords().stream().map(jobCourse -> {
			Course course = courseIdMap.get(jobCourse.getCourseId());
			JobCourseVO jobCourseVO = courseConvert.entityToJobCourseVO(course);
			jobCourseVO.setJobCourseId(jobCourse.getJobCourseId());
			jobCourseVO.setJobTypeId(jobTypeId);
			jobCourseVO.setIsMandatory(jobCourse.getIsMandatory());
			return jobCourseVO;
		}).toList();

		Page<JobCourseVO> voPage = new Page<>(pageInfo.getCurrent(), pageInfo.getSize(), jobCoursePage.getTotal());
		voPage.setRecords(voList);
		return voPage;

	}

}
