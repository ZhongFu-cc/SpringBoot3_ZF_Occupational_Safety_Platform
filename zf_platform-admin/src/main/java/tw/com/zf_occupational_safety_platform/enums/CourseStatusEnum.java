package tw.com.zf_occupational_safety_platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseStatusEnum {

	NOT_STARTED("not_started", "未開始", "Not-Started"), IN_PROGRESS("in_progress", "進行中", "In-Progress"),
	COMPLETED("completed", "已完成", "Completed"), EXPIRED("expired", "已過期", "Expired"),
	CANCELLED("cancelled", "已被取消", "cancelled");

	@EnumValue
	@JsonValue // JSON 輸出用
	private final String value;

	private final String labelZh;

	private final String labelEn;

	// 根據 值 獲取對應的枚舉常量
	@JsonCreator
	public static CourseStatusEnum fromValue(String value) {
		for (CourseStatusEnum courseContentTypeEnum : values()) {
			if (courseContentTypeEnum.value.equals(value))
				return courseContentTypeEnum;
		}
		throw new IllegalArgumentException("無效的值: " + value);
	}

}
