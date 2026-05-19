package tw.org.topbs.convert;

import org.mapstruct.Mapper;

import tw.org.topbs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.org.topbs.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.org.topbs.pojo.entity.Tag;

@Mapper(componentModel = "spring")
public interface TagConvert {

	Tag addDTOToEntity(AddTagDTO addTagDTO);
	
	Tag putDTOToEntity(PutTagDTO updateTagDTO);
	
}
