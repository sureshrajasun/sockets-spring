package com.thegeekyasian;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SocketsSpringApplication {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(SocketsSpringApplication.class, args);
	}

	@Bean
	public Map<String, String> myMap() {
		final Map<String, String> myMap = new HashMap<>();
		myMap.put("A", "a");
		return myMap;
	}

	@Bean
	public CamelContext camelContext() {
		return new SpringCamelContext(applicationContext);
	}
}

