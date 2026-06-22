package tw.com.zf_occupational_safety_platform.system.convert;

import org.mapstruct.Mapper;

import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeExcel;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;

@Mapper(componentModel = "spring")
public interface SysUserConvert {

	SysUser addDTOToEntity(AddSysUserDTO addSysUserDTO);
	
	SysUser putDTOToEntity(PutSysUserDTO putSysUserDTO);
	
	//最後返回為SysUserVo對象, 方法名為entityToVO, 參數為SysUser對象
	SysUserVO entityToVO(SysUser sysUser);
	
	SysUser employeeExcelToEntity(EmployeeExcel employeeExcel);
	
	StagingSysUser employeeExcelToStaging(EmployeeExcel employeeExcel);
	
}
