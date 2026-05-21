package tw.com.zf_occupational_safety_platform.service;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;

/**
 * <p>
 * 課程類別 與 法規限制表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-05-21
 */
public interface CourseCategoryService extends IService<CourseCategory> {

	CourseCategory get(Long courseCategoryId);

	CourseCategory create(AddCourseCategoryDTO addCourseCategoryDTO);

	void update(PutCourseCategoryDTO putCourseCategoryDTO);

	void remove(Long courseCategoryId);

}
