package com.example.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import lombok.extern.java.Log;

/*
 * SavedRequestAwareAuthenticationSuccessHandler 클래스는 determineTargetUrl 메소드를 오버라이드해서 redirect의 URL을 결정해 준다.
 * 
 * LoginSeccessHandler 클래스의 핵심 역할은 HttpSession에 dest 값이 존재하면 redirect의 경로를 dest 값으로 지정해준다.
 * dest 값이 존재하지 않다면 기존 방식으로 동작한다.
 * */
@Log
public class LoginSeccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		log.info("----------------determineTargetUrl-----------------");
		Object dest = request.getSession().getAttribute("dest");
		String nextURL = null;
		
		if (dest != null) {
			request.getSession().removeAttribute("dest");
			nextURL = (String) dest;
		} else {
			nextURL = super.determineTargetUrl(request, response);
		}
		
		log.info("----------------" + nextURL + "-----------------");
		return nextURL;
	}
	
}
