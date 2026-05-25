package tw.com.zf_occupational_safety_platform.system.pojo.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class UpdateUserStatus {

    @NotNull
    private Long sysUserId;

    @NotNull
    private CommonStatusEnum status;

	
}
