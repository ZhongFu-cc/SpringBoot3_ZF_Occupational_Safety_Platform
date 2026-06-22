package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.AccountPasswordWrongException;
import tw.com.zf_occupational_safety_platform.exception.AccountWrongException;
import tw.com.zf_occupational_safety_platform.system.convert.SysUserConvert;
import tw.com.zf_occupational_safety_platform.system.mapper.SysUserMapper;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.LoginInfo;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;
import tw.com.zf_occupational_safety_platform.system.service.SysUserService;

/**
 * <p>
 * 用戶表 - 存取系統用戶個人信息 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */

@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	private final SysUserConvert sysUserConvert;

	@Override
	public boolean validAccountExist(SysUser sysUser) {
		// 已存在為true , 不存在為false
		return baseMapper.selectByAccount(sysUser.getAccount()) != null;
	}

	@Override
	public boolean validEmailExist(SysUser sysUser) {
		// 已存在為true , 不存在為false
		return baseMapper.selectByEmail(sysUser.getEmail()) != null;
	}

	@Override
	public SysUser get(Long id) {
		return baseMapper.selectById(id);
	}

	@Override
	public SysUser getByIdAndCompany(Long id, Long companyId) {
		return baseMapper.selectByIdAndCompanyId(id, companyId);
	}

	@Override
	public List<SysUser> list() {
		return baseMapper.selectList(null);
	}

	@Override
	public List<SysUser> findByDepartments(Collection<Long> departmentIds) {
		if (departmentIds == null || departmentIds.isEmpty()) {
			return Collections.emptyList();
		}
		return baseMapper.selectByDepartmentIds(departmentIds);
	}

	@Override
	public IPage<SysUser> findDirectChild(IPage<SysUser> pageInfo, Long parentId, String queryText) {
		return baseMapper.selectByParentIdAndQuery(pageInfo, parentId, queryText);
	}

	@Override
	public IPage<SysUser> findByCompany(IPage<SysUser> pageInfo, Long parentId, Long companyId, String queryText) {
		return baseMapper.selectByParentIdAndQueryExcludeCompanyId(pageInfo, parentId, companyId, queryText);
	}

	@Override
	public SysUser create(AddSysUserDTO addSysUserDTO) {
		SysUser sysUser = sysUserConvert.addDTOToEntity(addSysUserDTO);
		baseMapper.insert(sysUser);
		return sysUser;

	}

	@Override
	public void insertFromStaging(String batchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(PutSysUserDTO putSysUserDTO) {
		SysUser sysUser = sysUserConvert.putDTOToEntity(putSysUserDTO);
		baseMapper.updateById(sysUser);
	}

	@Override
	public void remove(Long id) {
		baseMapper.deleteById(id);
	}

	@Override
	public SysUser login(LoginInfo loginInfo) {

		// 登入校驗查詢，獲取用戶資料
		LambdaQueryWrapper<SysUser> loginQueryWrapper = new LambdaQueryWrapper<>();
		loginQueryWrapper.eq(SysUser::getAccount, loginInfo.getAccount())
				.eq(SysUser::getPassword, loginInfo.getPassword());

		SysUser sysUser = baseMapper.selectOne(loginQueryWrapper);
		if (sysUser == null) {
			throw new AccountPasswordWrongException("帳號或密碼錯誤");
		}

		if (CommonStatusEnum.NO.equals(sysUser.getIsActive())) {
			throw new AccountWrongException("此帳戶已被停用/凍結");
		}

		return sysUser;

	}

	@Override
	public void updateCompanyUserStatus(Long id, CommonStatusEnum activeStatus) {
		SysUser user = new SysUser();
		user.setSysUserId(id);
		user.setIsActive(activeStatus);
		baseMapper.updateById(user);
	}

	@Override
	public long countByDepartment(Long departmentId) {
		return baseMapper.countByDepartmentId(departmentId);
	}

}
