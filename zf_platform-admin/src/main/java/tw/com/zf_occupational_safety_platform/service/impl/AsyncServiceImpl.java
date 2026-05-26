package tw.com.zf_occupational_safety_platform.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.zf_occupational_safety_platform.helper.S3Helper;
import tw.com.zf_occupational_safety_platform.service.AsyncService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncServiceImpl implements AsyncService {

//	private final JavaMailSender mailSender;
	private final S3Helper s3Helper;
	

}
