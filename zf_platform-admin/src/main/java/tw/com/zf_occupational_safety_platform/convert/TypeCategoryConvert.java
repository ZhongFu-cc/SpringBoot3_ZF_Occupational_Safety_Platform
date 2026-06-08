package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTypeCategoryDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourseCategory;

@Mapper(componentModel = "spring")
public interface TypeCategoryConvert {

	JobTypeCourseCategory addDTOToEntity(AddTypeCategoryDTO addTypeCategoryDTO);

	JobTypeCourseCategory putDTOToEntity(PutTypeCategoryDTO putTypeCategoryDTO);

}
