package tw.com.zf_occupational_safety_platform.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.manager.CompanyRegisterManager;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutCompanyDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Company;
import tw.com.zf_occupational_safety_platform.service.CompanyService;
import tw.com.zf_occupational_safety_platform.utils.R;

/**
 * <p>
 * 公司表 前端控制器
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Tag(name = "公司 API")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/company")
public class CompanyController {

	private final CompanyService companyService;
	private final CompanyRegisterManager companyRegisterManager;

	/**
	 * 根據ID 查詢 公司
	 * 
	 * @param id
	 * @return
	 */
	@Operation(summary = "根據ID 查詢 公司")
	@GetMapping("{id}")
	@SaCheckRole("super-admin")
	public R<Company> getCompany(@PathVariable("id") @Schema(type = "string") Long id) {
		Company courseCategory = companyService.get(id);
		return R.ok(courseCategory);
	}

	/**
	 * 查詢 全部公司列表
	 * 
	 * @param queryText
	 * @return
	 */
	@GetMapping()
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 公司列表")
	@SaCheckRole("super-admin")
	public R<List<Company>> findCompanys(@RequestParam(required = false) String queryText) {
		List<Company> companys = companyService.findByQuery(queryText);
		return R.ok(companys);
	}

	/**
	 * 查詢 公司分頁對象
	 * 
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("pagination")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@Operation(summary = "查詢 公司分頁對象")
	@SaCheckRole("super-admin")
	public R<IPage<Company>> findCompanyPage(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) String queryText) {

		Page<Company> pageInfo = new Page<>(page, size);
		IPage<Company> courseCategoryPage = companyService.findPageByQuery(pageInfo, queryText);
		return R.ok(courseCategoryPage);
	}

	/**
	 * 新增 公司
	 * 
	 * @param addCompanyDTO
	 * @return
	 */
	@Operation(summary = "新增 公司")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PostMapping
	public R<Void> saveCompany(@RequestBody @Valid AddCompanyDTO addCompanyDTO) {
		companyRegisterManager.createCompany(addCompanyDTO);
		return R.ok();
	}

	/**
	 * 更新 公司
	 * 
	 * @param putCompanyDTO
	 * @return
	 */
	@Operation(summary = "更新 公司")
	@Parameters({
			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER) })
	@SaCheckRole("super-admin")
	@PutMapping
	public R<Void> updateCompany(@RequestBody @Valid PutCompanyDTO putCompanyDTO) {
		companyService.update(putCompanyDTO);
		return R.ok();
	}

	//	/**
	//	 * 根據ID 刪除 公司，暫時不讓他刪除
	//	 * 
	//	 * @param id
	//	 * @return
	//	 */
	//	@Operation(summary = "根據ID 刪除 公司 ")
	//	@Parameters({
	//			@Parameter(name = "Authorization", description = "請求頭token,token-value開頭必須為Bearer ", required = true, in = ParameterIn.HEADER), })
	//	@SaCheckRole("super-admin")
	//	@DeleteMapping("{id}")
	//	public R<Void> removeCompany(@PathVariable @Schema(type = "string") Long id) {
	//		companyService.remove(id);
	//		return R.ok();
	//	}

}
