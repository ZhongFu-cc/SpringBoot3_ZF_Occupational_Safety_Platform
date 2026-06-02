package tw.com.zf_occupational_safety_platform.service.impl;

import tw.com.zf_occupational_safety_platform.pojo.entity.JobType;
import tw.com.zf_occupational_safety_platform.mapper.JobTypeMapper;
import tw.com.zf_occupational_safety_platform.service.JobTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 作業類別 表 服务实现类
 * </p>
 *
 * @author Joey
 * @since 2026-06-02
 */
@Service
public class JobTypeServiceImpl extends ServiceImpl<JobTypeMapper, JobType> implements JobTypeService {

}
