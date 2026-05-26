package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.com.zf_occupational_safety_platform.convert.TagConvert;
import tw.com.zf_occupational_safety_platform.mapper.TagMapper;
import tw.com.zf_occupational_safety_platform.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.com.zf_occupational_safety_platform.pojo.entity.Tag;
import tw.com.zf_occupational_safety_platform.service.TagService;

/**
 * <p>
 * 標籤表,用於對Member進行分組 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2025-01-23
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

	private final TagConvert tagConvert;

	
	@Override
	public Tag getTag(Long tagId) {
		return baseMapper.selectById(tagId);
	}

	@Override
	public Long insertTag(AddTagDTO insertTagDTO) {
		Tag tag = tagConvert.addDTOToEntity(insertTagDTO);
		baseMapper.insert(tag);
		return tag.getTagId();
	}

	@Override
	public void updateTag(PutTagDTO updateTagDTO) {
		Tag tag = tagConvert.putDTOToEntity(updateTagDTO);
		baseMapper.updateById(tag);
	}

	@Override
	public void deleteTag(Long tagId) {
		baseMapper.deleteById(tagId);
	}



	private Tag createTag(String type, String name, String description, String color) {
		Tag tag = new Tag();
		tag.setType(type);
		tag.setName(name);
		tag.setDescription(description);
		tag.setStatus(0);
		tag.setColor(color);
		baseMapper.insert(tag);
		return tag;
	}


}
