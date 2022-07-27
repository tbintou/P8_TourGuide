package com.bintou.guide.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Locale;

@SpringBootApplication
@EnableFeignClients
public class GuideProjectApplication {
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		SpringApplication.run(GuideProjectApplication.class, args);
	}

}
