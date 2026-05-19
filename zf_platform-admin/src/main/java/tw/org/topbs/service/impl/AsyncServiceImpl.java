package tw.org.topbs.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.org.topbs.service.AsyncService;
import tw.org.topbs.utils.S3Util;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncServiceImpl implements AsyncService {

	private final JavaMailSender mailSender;
	private final S3Util s3Util;
	

}
