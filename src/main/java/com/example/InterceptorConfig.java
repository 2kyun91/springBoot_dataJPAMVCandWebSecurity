package com.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.LoginCheckInterceptor;

import lombok.extern.java.Log;

/*
 * 스프링 부트 프로젝트에 MVC 관련된 설정을 하기 위해서는 WebMvcConfigurerAdapter라는 추상 클래스를 이용했었다.
 * 이 추상 클래스는 버전이 바뀌면서 더 이상 사용되지 않는데 클래스의 모든 내용을 WebMvcConfigurer 인터페이스에 default로 구현된 형태로 변경되었다.
 * 따라서 WebMvcConfigurer 인터페이스를 상속하고 필요한 메소드를 오버라이드한다.
 * */
@Configuration
@Log
public class InterceptorConfig implements WebMvcConfigurer{
	
	// 어느 URL에 동작하는지 addPathPatterns()로 설정
	@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/login");
			WebMvcConfigurer.super.addInterceptors(registry);
		}
	
}
