package com.maurosalani.project.attsd.web_config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
		registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
	}
}