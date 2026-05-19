package tw.org.topbs.validation.constraint;

import tw.org.topbs.enums.FormFieldTypeEnum;
import tw.org.topbs.pojo.DTO.FormFieldOptionDTO;

public interface HasFieldOptions {

	public FormFieldTypeEnum getFieldType();
	
	public FormFieldOptionDTO getOptions();
	
}
