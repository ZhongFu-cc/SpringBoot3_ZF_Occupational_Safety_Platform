package tw.com.zf_occupational_safety_platform.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.exception.AccountPasswordWrongException;
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
	public SysUser get(Long id) {
		return baseMapper.selectById(id);
	}

	@Override
	public List<SysUser> list() {
		return baseMapper.selectList(null);
	}

	@Override
	public SysUser create(AddSysUserDTO addSysUserDTO) {
		SysUser sysUser = sysUserConvert.addDTOToEntity(addSysUserDTO);
		baseMapper.insert(sysUser);
		return sysUser;

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
			throw new AccountPasswordWrongException("此帳戶已被停用");
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

}
