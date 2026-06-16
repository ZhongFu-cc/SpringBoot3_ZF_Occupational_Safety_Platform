package tw.com.zf_occupational_safety_platform.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChapterContentTypeEnum {

	DIRECTORY("directory", "目錄", "Directory"), VIDEO("video", "影片", "Video"), QUIZ("quiz", "總測驗", "Quiz");
//	SURVEY("survey", "課程總測驗", "Survey");

	@EnumValue
	@JsonValue // JSON 輸出用
	private final String value;

	private final String labelZh;

	private final String labelEn;

	// 根據 值 獲取對應的枚舉常量
	@JsonCreator
	public static ChapterContentTypeEnum fromValue(String value) {
		for (ChapterContentTypeEnum courseContentTypeEnum : values()) {
			if (courseContentTypeEnum.value.equals(value))
				return courseContentTypeEnum;
		}
		throw new IllegalArgumentException("無效的值: " + value);
	}

}
