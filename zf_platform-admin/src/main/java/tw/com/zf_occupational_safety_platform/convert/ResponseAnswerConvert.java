package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddResponseAnswerDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutResponseAnswerDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.ResponseAnswer;

@Mapper(componentModel = "spring")
public interface ResponseAnswerConvert {

    // 宣告默認映射 , 告訴 MapStruct 如何把 CommonStatusEnum → Integer
//    default Integer commonStatusEnumMapToInteger(CommonStatusEnum status) {
//        return status == null ? null : status.getValue();
//    }
	
	ResponseAnswer addDTOToEntity(AddResponseAnswerDTO responseAnswerDTO);
	
	ResponseAnswer putDTOToEntity(PutResponseAnswerDTO putResponseAnswerDTO);
	
	PutResponseAnswerDTO entityToPutDTO(ResponseAnswer responseAnswer);
	
	
}
