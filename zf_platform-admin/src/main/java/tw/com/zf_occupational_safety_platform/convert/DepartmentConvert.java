package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;

@Mapper(componentModel = "spring")
public interface DepartmentConvert {

	Department addDTOToEntity(AddDepartmentDTO addDepartmentDTO);

	Department putDTOToEntity(PutDepartmentDTO putDepartmentDTO);

}
