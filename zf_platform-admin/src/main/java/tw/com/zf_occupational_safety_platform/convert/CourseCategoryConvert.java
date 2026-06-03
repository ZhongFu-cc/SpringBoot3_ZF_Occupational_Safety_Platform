package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.TypeCategoryVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseCategory;

@Mapper(componentModel = "spring")
public interface CourseCategoryConvert {

	CourseCategory addDTOToEntity(AddCourseCategoryDTO addCourseCategoryDTO);

	CourseCategory putDTOToEntity(PutCourseCategoryDTO putCourseCategoryDTO);

	TypeCategoryVO entityToVO(CourseCategory courseCategory);
}
