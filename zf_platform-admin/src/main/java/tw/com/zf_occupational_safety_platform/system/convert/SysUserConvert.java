package tw.com.zf_occupational_safety_platform.system.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import tw.com.zf_occupational_safety_platform.pojo.entity.StagingSysUser;
import tw.com.zf_occupational_safety_platform.pojo.excel.EmployeeExcel;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.EmployeeVO;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;

@Mapper(componentModel = "spring")
public interface SysUserConvert {

	SysUser addDTOToEntity(AddSysUserDTO addSysUserDTO);
	
	SysUser putDTOToEntity(PutSysUserDTO putSysUserDTO);
	
	//最後返回為SysUserVo對象, 方法名為entityToVO, 參數為SysUser對象
	SysUserVO entityToVO(SysUser sysUser);

	//企業員工對象, 部門名稱由 Manager 層補上
	EmployeeVO entityToEmployeeVO(SysUser sysUser);
	
	SysUser employeeExcelToEntity(EmployeeExcel employeeExcel);
	
	StagingSysUser employeeExcelToStaging(EmployeeExcel employeeExcel);

	//臨時表轉正式表對象, staging_sys_user_id 轉存後就是 sys_user_id
	//僅供轉存後接續指派角色與報名課程使用, 不會再寫回DB
	@Mapping(target = "sysUserId", source = "stagingSysUserId")
	SysUser stagingToEntity(StagingSysUser stagingSysUser);

	List<SysUser> stagingToEntity(List<StagingSysUser> stagingSysUsers);

}
