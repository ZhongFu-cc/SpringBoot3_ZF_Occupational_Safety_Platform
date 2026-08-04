package tw.com.zf_occupational_safety_platform.system.pojo.VO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

/**
 * 傳給前端的企業員工對象<br>
 * 相較於 SysUser，額外帶出部門名稱，且不回傳密碼
 *
 */
@Data
public class EmployeeVO {

	@Schema(description = "主鍵ID", type = "string")
	private Long sysUserId;

	@Schema(description = "父級ID", type = "string")
	private Long parentId;

	@Schema(description = "公司ID", type = "string")
	private Long companyId;

	@Schema(description = "公司名稱")
	private String companyName;

	@Schema(description = "部門ID", type = "string")
	private Long departmentId;

	@Schema(description = "部門名稱")
	private String departmentName;

	@Schema(description = "帳號")
	private String account;

	@Schema(description = "真實姓名")
	private String realName;

	@Schema(description = "信箱")
	private String email;

	@Schema(description = "電話號碼")
	private String phone;

	@Schema(description = "是否啟用;0=否,1=是")
	private CommonStatusEnum isActive;

	@Schema(description = "備註,通常寫這個使用者的主要角色")
	private String remark;

	@Schema(description = "創建時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	@Schema(description = "更新時間")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

}
