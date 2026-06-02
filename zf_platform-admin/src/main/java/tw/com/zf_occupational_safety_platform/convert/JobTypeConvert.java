package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutJobTypeDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;

@Mapper(componentModel = "spring")
public interface JobTypeConvert {

	JobType addDTOToEntity(AddJobTypeDTO addJobTypeDTO);

	JobType putDTOToEntity(PutJobTypeDTO putJobTypeDTO);

}
