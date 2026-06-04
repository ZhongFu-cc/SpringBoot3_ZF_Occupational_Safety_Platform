package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.VO.CompanyCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Course;

@Mapper(componentModel = "spring")
public interface CourseConvert {
	
	Course addDTOToEntity(AddCourseDTO addCourseDTO);
	
	Course putDTOToEntity(PutCourseDTO putCourseDTO);
	
	CompanyCourseVO entityToCompanyCourseVO(Course course);
	
	
}
