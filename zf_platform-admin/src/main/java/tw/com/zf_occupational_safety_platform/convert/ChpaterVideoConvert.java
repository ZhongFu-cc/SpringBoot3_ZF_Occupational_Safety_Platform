package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutChapterVideoDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ChapterVideo;

@Mapper(componentModel = "spring")
public interface ChpaterVideoConvert {

	ChapterVideo addDTOToEntity(AddChapterVideoDTO addChapterVideoDTO);

	ChapterVideo putDTOToEntity(PutChapterVideoDTO putChapterVideoDTO);

}
