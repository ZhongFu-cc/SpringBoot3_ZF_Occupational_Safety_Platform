package tw.org.topbs.service;

import com.baomidou.mybatisplus.extension.service.IService;

import tw.org.topbs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.org.topbs.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.org.topbs.pojo.entity.Tag;

/**
 * <p>
 * 標籤表,用於對Member進行分組 服务类
 * </p>
 *
 * @author Joey
 * @since 2025-01-23
 */
public interface TagService extends IService<Tag> {

	/**
	 * 獲取單一標籤
	 * 
	 * @param tagId
	 * @return
	 */
	Tag getTag(Long tagId);

	/**
	 * 新增標籤，返回tagId
	 * 
	 * @param addTagDTO
	 * @return
	 */
	Long insertTag(AddTagDTO addTagDTO);

	/**
	 * 更新標籤
	 * 
	 * @param putTagDTO
	 */
	void updateTag(PutTagDTO putTagDTO);

	/**
	 * 根據tagId刪除標籤
	 * 
	 * @param tagId
	 */
	void deleteTag(Long tagId);
}
