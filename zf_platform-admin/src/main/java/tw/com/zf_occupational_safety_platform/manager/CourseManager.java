package tw.com.zf_occupational_safety_platform.manager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.CourseConvert;
import tw.com.zf_occupational_safety_platform.exception.CourseException;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;
import tw.com.zf_occupational_safety_platform.service.CourseCategoryService;
import tw.com.zf_occupational_safety_platform.service.CourseService;

@Component
@RequiredArgsConstructor
public class CourseManager {

	// 「預設」存储桶名称
	@Value("${spring.cloud.aws.s3.bucketName}") // 注意：这里的 Value key 可能需要对应您的配置
	private String bucketName;

	private final static String COVER_IMAGE_BASE_PATH = "course/cover-image";
	private final CourseCategoryService courseCategoryService;
	private final CourseService courseService;
	private final CourseConvert courseConvert;
	private final S3Helper s3Helper;

	/**
	 * 查詢課程VO , 包含課程類別
	 */
	public IPage<CourseVO> findCourseVOPage(Page<Course> pageInfo, Long courseCategoryId, String queryText) {

		// 獲取所有類別的主鍵映射
		Map<Long, CourseCategory> CourseCategoryMapById = courseCategoryService.mapById();

		IPage<Course> coursePage = courseService.findPageByQuery(pageInfo, courseCategoryId, queryText);

		List<CourseVO> voList = coursePage.getRecords().stream().map(course -> {
			CourseVO vo = courseConvert.entityToVO(course);
			CourseCategory courseCategory = CourseCategoryMapById.get(course.getCourseCategoryId());
			vo.setCourseCategoryName(courseCategory.getName());
			return vo;
		}).toList();

		IPage<CourseVO> voPage = new Page<>(coursePage.getCurrent(), coursePage.getSize(), coursePage.getTotal());
		voPage.setRecords(voList);
		return voPage;

	}

	/**
	 * 創建課程
	 * 
	 * @param addCourseDTO
	 * @param imgFile
	 */
	public Course createCourse(AddCourseDTO addCourseDTO, MultipartFile imgFile) {

		// 沒檔案直接拋出錯誤
		if (!S3Helper.hasFile(imgFile)) {
			throw new CourseException("課程封面圖檔必須上傳");
		}

		// 檔案上傳到S3
		String s3Key = s3Helper.upload(COVER_IMAGE_BASE_PATH, imgFile.getOriginalFilename(), imgFile);

		// 資料轉換
		Course course = courseConvert.addDTOToEntity(addCourseDTO);
		// 塞入縮圖URL
		course.setCoverImage(s3Key);

		// 創建課程
		courseService.save(course);

		return course;
	}

	/**
	 * 更新課程
	 * 
	 * @param putCourseDTO
	 * @param imgFile
	 */
	public void updateCourse(PutCourseDTO putCourseDTO, MultipartFile imgFile) {

		// 要更新的Entity 
		Course targetCourse = courseConvert.putDTOToEntity(putCourseDTO);

		// 有檔案再更新進DB前更新coverImage路徑
		if (S3Helper.hasFile(imgFile)) {

			// 查詢原檔案路徑 , 並進行刪除
			Course OldCourse = courseService.get(targetCourse.getCourseId());
			String coverImagePath = OldCourse.getCoverImage();
			s3Helper.removeFile(bucketName, coverImagePath);

			// 上傳新的檔案並獲得S3 key
			String s3Key = s3Helper.upload(COVER_IMAGE_BASE_PATH, imgFile.getOriginalFilename(), imgFile);
			targetCourse.setCoverImage(s3Key);
		}
		

		courseService.saveOrUpdate(targetCourse);

	}

}
