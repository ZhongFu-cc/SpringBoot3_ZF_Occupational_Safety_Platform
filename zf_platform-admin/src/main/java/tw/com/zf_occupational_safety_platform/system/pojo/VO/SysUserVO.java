package tw.com.zf_occupational_safety_platform.system.pojo.VO;

import java.util.List;

import cn.dev33.satoken.stp.SaTokenInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;
import tw.com.zf_occupational_safety_platform.system.pojo.BO.RouteBO;

/**
 * 傳給前端的系統使用者對象
 * 
 */
@Data
public class SysUserVO {

	@Schema(description = "主鍵ID")
	private Long sysUserId;

	@Schema(description = "父級ID")
	private Long parentId;

	@Schema(description = "帳號")
	private String account;

	@Schema(description = "密碼")
	private String password;

	@Schema(description = "真實姓名")
	private String realName;

	@Schema(description = "信箱")
	private String email;

	@Schema(description = "電話號碼")
	private String phone;

	@Schema(description = "公司名稱")
	private String companyName;

	@Schema(description = "是否啟用;0=否,1=是")
	private CommonStatusEnum isActive;

	@Schema(description = "備註,通常寫這個使用者的主要角色")
	private String remark;

	@Schema(description = "token信息")
	private SaTokenInfo saTokenInfo;

	//權限集合
	@Schema(description = "持有角色集合")
	private List<String> roleList;
	@Schema(description = "持有權限集合")
	private List<String> permissionList;
	
	//路由集合
	@Schema(description = "路由集合")
	private List<RouteBO> routeList;

}
