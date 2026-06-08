package tw.com.zf_occupational_safety_platform.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyConvert {

	Company addDTOToEntity(AddCompanyDTO addCompanyDTO);

	Company putDTOToEntity(PutCompanyDTO putCompanyDTO);

}
