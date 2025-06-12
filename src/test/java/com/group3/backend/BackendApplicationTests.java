package com.group3.backend;
import org.junit.jupiter.api.Test;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
		SwaggerConfig.class,
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
