package tw.com.zf_occupational_safety_platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompanyStatusEnum {

	ENABLED("enabled", "啟用", "Enabled"), DISABLED("disabled", "禁用", "Disabled"), EXPIRED("expired", "逾期未繳費", "Expired");

	@EnumValue
	@JsonValue // JSON 輸出用
	private final String value;

	private final String labelZh;

	private final String labelEn;

	// 根據 值 獲取對應的枚舉常量
	@JsonCreator
	public static CompanyStatusEnum fromValue(String value) {
		for (CompanyStatusEnum courseContentTypeEnum : values()) {
			if (courseContentTypeEnum.value.equals(value))
				return courseContentTypeEnum;
		}
		throw new IllegalArgumentException("無效的值: " + value);
	}

}
