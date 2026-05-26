package tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.com.zf_occupational_safety_platform.enums.CommonStatusEnum;

@Data
public class PutCourseCategoryDTO {

	@NotNull
    @Schema(description = "主鍵ID")
    private Long courseCategoryId;

    @NotBlank
    @Schema(description = "類別名稱 (例如: 基礎職安、高分貝作業環境、高溫環境)")
    private String name;

    @Schema(description = "類別代碼 (用於系統識別或法規綁定代號)")
    private String code;

    @Schema(description = "類別描述")
    @NotBlank
    private String description;

    @Schema(description = "台灣職安法規最低法定時數限制 (單位-分鐘)")
    private Integer minRequiredMinutes;

    @NotNull
    @Schema(description = "是否啟用;0=否,1=是")
    private CommonStatusEnum isActive;
	
}
