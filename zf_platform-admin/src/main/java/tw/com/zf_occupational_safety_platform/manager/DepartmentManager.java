package tw.com.zf_occupational_safety_platform.manager;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;

/**
 * 部門 - 管理層
 */
@Component
@RequiredArgsConstructor
public class DepartmentManager {

	private final DepartmentService departmentService;

	public void createDepartment(AddDepartmentDTO addDepartmentDTO, SysUserVO operator) {
		addDepartmentDTO.setCompanyId(operator.getCompanyId());
		departmentService.create(addDepartmentDTO);
	}
	
	public void updateDepartment(PutDepartmentDTO putDepartmentDTO, SysUserVO operator) {
		
	}

}
