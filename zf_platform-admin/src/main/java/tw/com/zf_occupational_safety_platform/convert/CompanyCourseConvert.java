package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;

@Mapper(componentModel = "spring")
public interface CompanyCourseConvert {

	CompanyCourse addDTOToEntity(AddCompanyCourseDTO addCompanyCourseDTO);


}
