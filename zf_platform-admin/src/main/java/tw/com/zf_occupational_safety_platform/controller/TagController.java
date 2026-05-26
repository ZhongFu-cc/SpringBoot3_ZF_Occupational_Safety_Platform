package tw.com.zf_occupational_safety_platform.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Tag;
import tw.com.zf_occupational_safety_platform.service.TagService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 標籤表,用於對Member進行分組 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2025-01-23
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "標籤API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tag")
public class TagController {

	private final TagService tagService;

	@GetMapping("{id}")
	@Operation(summary = "查詢單一標籤")
	public R<Tag> getTag(@PathVariable("id") Long tagId) {
		Tag tag = tagService.getTag(tagId);
		return R.ok(tag);
	}



	@Operation(summary = "新增標籤")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckLogin
	@PostMapping
	public R<Void> saveTag(@RequestBody AddTagDTO insertTagDTO) {
		tagService.insertTag(insertTagDTO);
		return R.ok();

	}

	@Operation(summary = "更新標籤")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckLogin
	@PutMapping
	public R<Void> updateTag(@RequestBody PutTagDTO updateTagDTO) {
		tagService.updateTag(updateTagDTO);
		return R.ok();

	}

	@Operation(summary = "刪除標籤")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckLogin
	@DeleteMapping("{id}")
	public R<Void> deleteTag(@PathVariable("id") Long tagId) {
		tagService.deleteTag(tagId);
		return R.ok();

	}



}
