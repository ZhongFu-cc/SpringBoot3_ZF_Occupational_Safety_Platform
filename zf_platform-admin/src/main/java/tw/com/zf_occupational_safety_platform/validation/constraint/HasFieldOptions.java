package tw.com.zf_occupational_safety_platform.validation.constraint;

import tw.com.zf_occupational_safety_platform.enums.FormFieldTypeEnum;
import tw.com.zf_occupational_safety_platform.pojo.DTO.FormFieldOptionDTO;

public interface HasFieldOptions {

	public FormFieldTypeEnum getFieldType();
	
	public FormFieldOptionDTO getOptions();
	
}
