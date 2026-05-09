package io.moup.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MoupApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoupApiApplication.class, args);
	}

}
