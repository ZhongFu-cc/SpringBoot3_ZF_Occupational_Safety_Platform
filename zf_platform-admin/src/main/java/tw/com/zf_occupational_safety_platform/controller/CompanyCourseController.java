package tw.com.zf_occupational_safety_platform.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.CompanyCourseManager;
import tw.com.zf_occupational_safety_platform.pojo.VO.CompanyCourseVO;
import tw.com.zf_occupational_safety_platform.pojo.entity.CompanyCourse;
import tw.com.zf_occupational_safety_platform.system.manager.AuthManager;
import tw.com.zf_occupational_safety_platform.system.pojo.VO.SysUserVO;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 企業持有課程 表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "企業課程 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/company-course")
public class CompanyCourseController {

	private final AuthManager authManager;
	private final CompanyCourseManager companyCourseManager;

	/**
	 * 臨時AddDTO, 用於新增 公司x課程 關聯 <br>
	 */
	public record AddCompanyCourse(@NotNull @Schema(description = "課程 ID", type = "string") Long courseId) {
	}

	/**
	 * 臨時PutDTO, 用於移除 公司x課程 關聯 <br>
	 */
	public record PutCompanyCourse(@NotNull @Schema(description = "企業x課程 主鍵ID", type = "string") Long companyCourseId) {

	}

	/**
	 * 根據ID 查詢 企業課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 企業課程")
	@GetMapping("{id}")
	@SaCheckRole("company_manager")
	public R<CompanyCourseVO> getCompanyCourse(@PathVariable("id") @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		CompanyCourseVO vo = companyCourseManager.getCompanyCourseVO(id, sysUserVO);
		return R.ok(vo);
	}

	/**
	 * 查詢 企業課程 列表對象
	 * 
	 * @return
	 */
	@GetMapping
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 企業課程 列表對象")
	@SaCheckRole("company_manager")
	public R<List<CompanyCourseVO>> findCompanyCourseList() {
		SysUserVO sysUserVO = authManager.getUserInfo();
		List<CompanyCourseVO> voList = companyCourseManager.findCompanyCourseVOList(sysUserVO);
		return R.ok(voList);
	}

	/**
	 * 查詢 企業課程 分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 企業課程 分頁對象")
	@SaCheckRole("company_manager")
	public R<IPage<CompanyCourseVO>> findCompanyCoursePage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) Long courseCategoryId,
			@RequestParam(required = false) String queryText) {

		SysUserVO sysUserVO = authManager.getUserInfo();
		Page<CompanyCourse> pageInfo = new Page<>(page, size);
		IPage<CompanyCourseVO> voPage = companyCourseManager.findCompanyCourseVOPage(pageInfo,courseCategoryId, queryText, sysUserVO);

		return R.ok(voPage);
	}

	/**
	 * 新增 課程 到 企業課程
	 * 
	 * @param addDepartmentDTO
	 * @return
	 */
	@Operation(summary = "新增 課程 到 企業課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("company_manager")
	@PostMapping
	public R<Void> addCourse2Company(@RequestBody @Valid AddCompanyCourse addCompanyCourse) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyCourseManager.addCourse2Company(addCompanyCourse, sysUserVO);
		return R.ok();
	}

	/**
	 * 從 企業課程 移除 課程
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "從 企業課程 移除 課程")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	@SaCheckRole("company_manager")
	@DeleteMapping("{id}")
	public R<Void> removeCourseFromCompany(@PathVariable @Schema(type = "string") Long id) {
		SysUserVO sysUserVO = authManager.getUserInfo();
		companyCourseManager.removeCourseFromCompany(id, sysUserVO);
		return R.ok();
	}

}
