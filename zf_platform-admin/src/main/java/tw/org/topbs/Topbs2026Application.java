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
public class Topbs2026Application {
	public static void main(String[] args) {
		SpringApplication.run(Topbs2026Application.class, args);
	}
}
