package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCourseChapterDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CourseChapter;

@Mapper(componentModel = "spring")
public interface CourseChapterConvert {
	
	CourseChapter addDTOToEntity(AddCourseChapterDTO addCourseChapterDTO);
	
	CourseChapter putDTOToEntity(PutCourseChapterDTO putCourseChapterDTO);
	
	
}
