package com.supportportal;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@Log4j2
public class SupportportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportportalApplication.class, args);
		log.info("===========================================");
		log.info("========  Support Portal RUNNING  ========");
		log.info("===========================================");
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
