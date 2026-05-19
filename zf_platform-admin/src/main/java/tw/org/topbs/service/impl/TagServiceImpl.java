package tw.org.topbs.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import tw.org.topbs.convert.TagConvert;
import tw.org.topbs.mapper.TagMapper;
import tw.org.topbs.pojo.DTO.addEntityDTO.AddTagDTO;
import tw.org.topbs.pojo.DTO.putEntityDTO.PutTagDTO;
import tw.org.topbs.pojo.entity.Tag;
import tw.org.topbs.service.TagService;

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
