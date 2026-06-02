package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.DepartmentCourse;

@Mapper(componentModel = "spring")
public interface DepartmentCourseConvert {

	DepartmentCourse addDTOToEntity(AddDepartmentCourseDTO addDepartmentCourseDTO);


}
