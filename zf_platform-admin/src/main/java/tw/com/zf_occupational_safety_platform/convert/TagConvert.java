package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Tag;

@Mapper(componentModel = "spring")
public interface TagConvert {

	Tag addDTOToEntity(AddTagDTO addTagDTO);
	
	Tag putDTOToEntity(PutTagDTO updateTagDTO);
	
}
