package com.mss;

import com.mss.config.SupabaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SupabaseProperties.class)
public class MechanicalShopSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MechanicalShopSystemApplication.class, args);
	}

}
