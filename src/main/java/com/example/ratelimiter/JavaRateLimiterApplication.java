package com.example.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
public class JavaRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaRateLimiterApplication.class, args);
	}

	@Bean
	public RateLimiterConfig rateLimiterConfigBean(){
		RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
												.limitRefreshPeriod(Duration.ofMillis(1))
												.limitForPeriod(10)
												.timeoutDuration(Duration.ofMillis(25))
												.build();
		return rateLimiterConfig;
	}
}

