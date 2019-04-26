package com.example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.java.Log;

/*
 * 시나리오 : 게시물 조회 -> 수정 시 로그인 필요(로그인 하지 않은 상태)-> 로그인 화면 -> 로그인 완료 후 작업 중이던 화면 호출 
 * 위 시나리오를 구현하기 위해 인터샙터를 사용한다.
 * 인터셉터는 컨트롤러의 호출('/login')을 사전 혹은 사후에 가로챌 수 있고 
 * 컨트롤러가 HttpServletRequest나 HttpServletResponse를 이용하지 않는데 비해 인터셉터는 서블릿 자원들을 그대로 활용할 수 있다는 장점이 있다.
 * 
 * [구현 방식 순서도]
 *  - 경로를 호출할 때 특정 파라미터(dest)를 추가하고 파라미터의 값으로 로그인 후에 이동할 경로를 지정한다.
 *  - 호출을 감지하는 인터셉터를 추가해서 호출 시 파라미터 유/무를 확인, 존재한다면 HttpSession에 값을 추가한다.
 *  - 로그인 후 HttpSession에 특정 값이 보관되어 있으면 이를 이용해서 redirect.
 * */
@Log
public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("preHandle...");

		String dest = request.getParameter("dest"); // 특정 파라미터 생성

		if (dest != null) {
			request.getSession().setAttribute("dest", dest);
		}
		
		// preHandle()은 컨트롤러의 호출 전에 동작한다.
		return super.preHandle(request, response, handler);
	}

}
