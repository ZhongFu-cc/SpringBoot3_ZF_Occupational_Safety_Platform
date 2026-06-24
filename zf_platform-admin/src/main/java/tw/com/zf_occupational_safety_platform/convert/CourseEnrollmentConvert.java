package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.VO.CourseEnrollmentVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseEnrollment;

@Mapper(componentModel = "spring")
public interface CourseEnrollmentConvert {
	
	CourseEnrollmentVO entityToVO(CourseEnrollment courseEnrollment);
	
	
}
