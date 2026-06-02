package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.DepartmentConvert;
import tw.com.zf_occupational_safety_platform.mapper.DepartmentMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutDepartmentDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Department;
import tw.com.zf_occupational_safety_platform.service.DepartmentService;

/**
 * <p>
 * 部門表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

	private final DepartmentConvert departmentConvert;

	@Override
	public Department get(Long departmentId) {
		return baseMapper.selectById(departmentId);
	}

	@Override
	public IPage<Department> findPageByQuery(Page<Department> pageInfo, String queryText) {
		return baseMapper.selectByQuery(pageInfo, queryText);
	}

	@Override
	public Department create(AddDepartmentDTO addDepartmentDTO) {
		Department department = departmentConvert.addDTOToEntity(addDepartmentDTO);
		baseMapper.insert(department);
		return department;
	}

	@Override
	public void update(PutDepartmentDTO putDepartmentDTO) {
		Department department = departmentConvert.putDTOToEntity(putDepartmentDTO);
		baseMapper.updateById(department);
	}

	@Override
	public void remove(Long departmentId) {
		baseMapper.deleteById(departmentId);
	}

}
