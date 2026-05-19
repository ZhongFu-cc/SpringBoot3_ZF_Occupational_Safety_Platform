package tw.org.topbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("tw.org.topbs")
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ZF_PlatformApplication {
	public static void main(String[] args) {
		SpringApplication.run(ZF_PlatformApplication.class, args);
	}
}
