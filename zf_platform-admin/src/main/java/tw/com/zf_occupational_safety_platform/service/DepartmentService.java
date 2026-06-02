package tw.com.zf_occupational_safety_platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;

/**
 * <p>
 * 部門表 服务类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
public interface DepartmentService extends IService<Department> {

	Department get(Long departmentId);

	IPage<Department> findPageByQuery(Page<Department> pageInfo, String queryText);

	Department create(AddDepartmentDTO addDepartmentDTO);

	void update(PutDepartmentDTO putDepartmentDTO);

	void remove(Long departmentId);
	
}
