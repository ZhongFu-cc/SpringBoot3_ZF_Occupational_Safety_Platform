package tw.com.zf_occupational_safety_platform.validation.constraint;

import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

public interface HasLoginAndMultipleSubmissionRules {

	public CommonStatusEnum getRequireLogin();
	public CommonStatusEnum getAllowMultipleSubmissions();
	
}
