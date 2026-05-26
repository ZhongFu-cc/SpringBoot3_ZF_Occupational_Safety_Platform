package tw.com.zf_occupational_safety_platform.system.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.AddSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.LoginInfo;
import tw.com.zf_occupational_safety_platform.system.pojo.DTO.PutSysUserDTO;
import tw.com.zf_occupational_safety_platform.system.pojo.entity.SysUser;

/**
 * <p>
 * 用戶表 - 存取系統用戶個人信息 服务类
 * </p>
 *
 * @author Joey
 * @since 2024-05-10
 */
public interface SysUserService extends IService<SysUser> {

	/**
	 * 根據主鍵ID查詢使用者的資料
	 * 
	 * @param id 主鍵ID
	 * @return
	 */
	SysUser get(Long id);

	/**
	 * 查詢所有查詢使用者的資料
	 * 
	 * @return
	 */
	List<SysUser> list();

	/**
	 * 分頁查詢 - 直接子用戶
	 * 
	 * @param pageInfo  分頁資訊
	 * @param parentId  父級ID
	 * @param queryText 查詢文字條件
	 * @return
	 */
	IPage<SysUser> findDirectChild(IPage<SysUser> pageInfo, Long parentId, String queryText);

	/**
	 * 創建使用者
	 * 
	 * @param addSysUserDTO
	 */
	SysUser create(AddSysUserDTO addSysUserDTO);

	/**
	 * 更新使用者
	 * 
	 * @param putSysUserDTO
	 */
	void update(PutSysUserDTO putSysUserDTO);

	/**
	 * 根據主鍵ID刪除使用者
	 * 
	 * @param id 主鍵ID
	 * 
	 */
	void remove(Long id);

	/**
	 * 修改企業用戶啟用狀態
	 * 
	 * @param id           主鍵ID
	 * @param activeStatus 啟用狀態
	 */
	void updateCompanyUserStatus(Long id, CommonStatusEnum activeStatus);

	/**
	 * 系統管理者登入方法,返回token、角色、權限
	 * 前端傳來一個由email 和 password組裝的 LoginInfo對象 先判斷帳號密碼取得SysUser的ID
	 * 將組裝好的sysUserInfo 放到緩存中
	 * 
	 * @param LoginInfo
	 * @return SysUserVO
	 */

	/**
	 * 用戶登入<br>
	 * 前端傳遞 account 和 password 進行校驗
	 * 
	 * @param loginInfo
	 * @return
	 */
	SysUser login(LoginInfo loginInfo);

}
