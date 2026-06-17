package tw.com.zf_occupational_safety_platform.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

@Data
//表示這一欄的寬度為「20 個字元」每個單位是約 1 個英文字的寬度，20 大致對應圖片寬度約 200px
@ColumnWidth(80 / 4)
public class EmployeeExcel {

	@ExcelProperty("部門")
	private String department;
	
	@ExcelProperty("姓名")
	private String realName;
	
	@ExcelProperty("帳號")
	private String account;

	@ExcelProperty("密碼")
	private String password;

	@ExcelProperty("信箱")
	private String email;

	@ExcelProperty("電話號碼")
	private String phone;

}
