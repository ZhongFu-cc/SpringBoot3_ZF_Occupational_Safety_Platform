package tw.org.topbs.convert;

import org.mapstruct.Mapper;

import tw.org.topbs.enums.CommonStatusEnum;
import tw.org.topbs.pojo.DTO.addEntityDTO.AddFormDTO;
import tw.org.topbs.pojo.DTO.putEntityDTO.PutFormDTO;
import tw.org.topbs.pojo.VO.FormVO;
import tw.org.topbs.pojo.entity.Form;

@Mapper(componentModel = "spring")
public interface FormConvert {

    // 宣告默認映射 , 告訴 MapStruct 如何把 CommonStatusEnum → Integer
    default Integer commonStatusEnumMapToInteger(CommonStatusEnum status) {
        return status == null ? null : status.getValue();
    }
	
	Form addDTOToEntity(AddFormDTO addFormDTO);
	
	Form putDTOToEntity(PutFormDTO putFormDTO);
	
	FormVO entityToVO(Form form);
	
}
