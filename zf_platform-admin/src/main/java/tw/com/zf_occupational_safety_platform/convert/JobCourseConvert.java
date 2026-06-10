package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobCourseDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobTypeCourse;

@Mapper(componentModel = "spring")
public interface JobCourseConvert {

	JobTypeCourse addDTOToEntity(AddJobCourseDTO addJobCourseDTO);

	JobTypeCourse putDTOToEntity(PutJobCourseDTO putJobCourseDTO);

}
