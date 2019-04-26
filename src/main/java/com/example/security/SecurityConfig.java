package com.example.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import lombok.extern.java.Log;

/*
 * 시큐리티에 대한 설정 클래스
 * 이 클래스가 정상적으로 인식되도록 @EnableWebSecurity 어노테이션을 추가하고 설정을 담당하는 WebSecurityConfigurerAdapter 클래스를 상속한다.
 * 
 * 웹에서의 스프링 시큐리티는 기본적으로 '필터' 기반으로 동작한다.
 * 스프링 시큐리티 내부에는 상당히 많은 종류의 필터들이 이미 존재하고 있기 때문에 개발시에는 필터들의 설정을 조정하는 방식을 주로 사용하게 된다.
 * 
 * HttpSecurity는 웹과 관련된 다양한 보안 설정을 할 수 있다.
 * 특정 경로에 특정 권한을 가진 사용자만 접근할 수 있도록 설정하고 싶을 때에는 autorizeRequests()와 antMatchers()를 이용해서 경로를 설정할 수 있다.
 * 
 * 웹과 관련된 로그인 정보는 기본적으로 HttpSession을 이용한다.
 * HttpSession은 세션 쿠키라는 것을 이용하기 때문에 기존의 로그인 정보를 삭제해야하는 경우 브라우저를 완전히 종료하거나 세션쿠리를 삭제해야한다.
 * 
 * [주의!] 스프링 시큐리티가 적용되면 'POST' 방식으로 보내는 '모든' 데이터는 CSRF 토큰값이 필요하는 점을 명심해야 된다.
 * 		   인증 방식 구조 참고 - /img/Spring Security 인증방식 구조.jpg
 * */
@Log
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	// [주의!] configure() 메소드를 오버라이드하는 경우에는 HttpSecurity 클래스 타입을 파라미터로 처리하는 메소드를 선택해야 한다.
	@Override
		protected void configure(HttpSecurity http) throws Exception {
			log.info("security config...");
			
			/*
			 * authorizeRequests()는 시큐리티 처리에 HTTPServletRequest를 이용한다는 것을 의미한다.
			 * antMatchers()에서는 특정한 경로를 지정한다.
			 * permitAll()은 모든 사용자가 접근할 수 있다는것을 의미하고 hasRole()은 특정권한을 가진 사람만이 접근할 수 있다는것을 의미한다.
			 * hasAnyRole()을 이용하면 여러개의 Role을 설정할 수 있다.
			 * antMatchers()는 빌더 패턴으로 연속적으로 '.'을 이용하는 방법을 많이 사용한다.
			 * formLogin()는 기본적으로 <form> 태그가 있는 웹페이지를 만들어 준다.
			 * logout().invalidateHttpSession(true)는 세션 무효화 기능으로 로그아웃 시 HttpSession의 정보를 무효화 시키고 모든 쿠키를 삭제한다.
			 * 
			 * rememberMe().key("")는 로그인 유지 기능으로 쿠키의 값으로 암호화된 값을 전달하므로 암호의 '키(key)'를 지정하여 사용한다.
			 * 스프링 시큐리티는 'remember-me' 기능을 사용하기 위해서 2가지 저장 방식을 사용할 수 있다.
			 *  - Hash-based Token (default)
			 *  - Persistent Token
			 * 
			 * CSRF 토큰을 생성해서 객체(변수)에 담아 유지하려는 경우 아래 2가지 설정 중 하나를 지정해주어야 한다.(view.html의 'var csrf') 
			 *  - .and().csrf().csrfTokenRepository() 
			 *  - .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			 *  
			 * 만약 CSRF 토큰이 만들어지기 전에 ${_csrf}를 지정해서 사용하는 경우 에러가 발생하며 illegalStateException 메시지가 출력된다.
			 * 책(스프링 시큐리티 4)에서는 이런 설정 없이 진행하는데 현재 버전(스프링 시큐리티 5)으로 업그레이드 되면서 바뀐것 같다.
			 * 
			 * CookieCsrfTokenRepository.withHttpOnlyFalse()는 스프링 시큐리티의 CSRF 설정이 기본적으로 토큰을 HTTP 전용 쿠키에 저장하는데 
			 * JavaScript는 HTTP 전용 쿠키에 접근 할 수 없으므로 스프링에만 HTTP를 사용하지 않도록 설정한다.(앵귤러 Js + Spring Security에서 많이 사용된다.)
			 * */
			http.authorizeRequests().
				 antMatchers("/boards/list").permitAll().
				 antMatchers("/boards/register").
				 hasAnyRole("BASIC", "MANAGER", "ADMIN")
				 .and().csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository());
//				 .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
			
			http.formLogin().loginPage("/login").successHandler(new LoginSeccessHandler()); // '/login' 처리 후에는 작성한 LoginSeccessHandler를 이용한다.
			http.exceptionHandling().accessDeniedPage("/accessDenied"); // 접근 제한 페이지 설정.
			http.logout().logoutUrl("/logout").invalidateHttpSession(true); // 로그아웃 페이지를 따로 설정하고 싶은 경우.
			
			http.rememberMe().key("custom").
				 userDetailsService(customUserDetailsService).
				 tokenRepository(getJDBCRepository()).
				 tokenValiditySeconds(60*60*24); // 2) 사용자 정의
		}
	
//	@Bean
//	public PasswordEncoder noOpPasswordEncoder() {
//		return NoOpPasswordEncoder.getInstance();
//	}
//	
//	 /*
//	  * AuthenticationManagerBuilder를 주입해서 다양한 설정을 생성하여 로그인 인증에 대한 처리를 한다.
//	  * 스프링 시큐리티 버전 5이상 부터는 password 앞에 식별자 정보'{noop}'를 넣어 패스워드 저장 형식을 지켜야한다.
//	  * 
//	  * AuthenticationManagerBuilder를 이용해서 실제로 인증을 처리하는 인증 매니저를 생성한다.
//	  * 여러 종류의 인증 매니저들을 생성할 수 있는데 대표적으로 메모리를 이용하거나 JDBC,LDAP 등을 사용하는 인증 매니저들을 사용할 수 있고
//	  * 각 인증 매니저는 인증이라는 처리를 할 수 있도록 authenticate()라는 메소드를 구현한다.
//	  * 
//	  * 인증 매니저가 사용하는 UserDetailsService는 실제로 인증/인가 정보를 처리하는 주체를 의미한다.
//	  * UserDetailsService 인터페이스는 인증의 주체에 대한 정보를 가져오는 하나의 메소드만이 존재하는데
//	  * 단순히 loadUserByUsername()이라는 메소드만으로 상세정보를 조회하는 용도일뿐 추가적인 작업은 없다.
//	  * UserDetails 인터페이스 타입을 반환한다.
//	  * UserDetails는 '사용자의 계정 정보 + 사용자가 가진 권한 정보'의 묶음이다.
//	  */
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		log.info("buid Auth global...");
//		
//		// 메모리 기반의 인증 매니저를 생성
////		auth.inMemoryAuthentication().withUser("manager").password("{noop}1111").roles("MANAGER");
//		
//		/*
//		 * JDBC를 이용한 인증 처리
//		 * [스프링 시큐리티가 데이터베이스 연동하는 방법]
//		 * - 1) 직접 SQL 등을 지정해서 처리하는 방법
//		 * - 2) 기존에 작성된 Repository나 서비스 객체들을 이용해서 별도로 시큐리티 관련 서비스를 개발하는 방법(사용자 정의)
//		 * 
//		 * 1)을 이용하기 위해 DataSource 타입의 객체를 주입한다.
//		 * 사용자 계정 정보와 권한을 체크하는 부분에는 DataSource를 이용하고 SQL을 지정한다.
//		 * SQL문은 
//		 * 	1-1) 사용자 계정 정보를 이용해 필요한 정보를 가져오는 SQL과 
//		 *  1-2) 해당 사용자의 권한을 확인하는 SQL을 작성한다.
//		 * */
//		String query1 = "select u_id as username, u_pw as password, 'true' as enabled from tbl1_members where u_id = ?"; // 1-1 
//		String query2 = "select member as u_id, role_name as role from tbl1_member_roles where member = ?"; // 1-2
//		
//		/*
//		 * jdbcAuthentication() 메소드는 JdbcUserDetailsManagerConfigurer 객체를 반환한다.
//		 * 이 객체를 이용해 DataSource를 주입하고 SQL문을 파라미터로 전달하는 방식을 이용해 인증 매니저를 생성한다.
//		 * 핵심은 usersByUsernameQuery(), authoritiesByUsernameQuery() 함수로
//		 * username을 이용해서 특정한 인증 주체(사용자) 정보를 세팅 (usersByUsernameQuery())하고 
//		 * username을 이용해서 권한에 대한 정보를 조회한다(authoritiesByUsernameQuery()).
//		 * 
//		 * usersByUsernameQuery()를 이용하는 경우 username, password, enabled라는 컬럼의 데이터가 필요한데 실제 테이블의 칼럼명과 다를 경우 'as'를 적용해 동일하게 맞춰준다.
//		 * enabled 칼럼은 해당 계정이 사용 가능한지를 의미한다.
//		 * 
//		 * '/manager'라는 경로로 접근하려면 'ROLE_MANGER'라는 이름의 권한이 필요한데 DB 칼럼 값에는 'ROLE_'이라는 문자열은 없다.
//		 * 따라서 rolePrefix()라는 메소드를 이용해서 'ROLE_'라는 문자열을 붙여준다.
//		 * 
//		 * 스프링 시큐리티 5버전 부터는 passwordEncoder(암호화)를 무조건적으로 설정해주어야 하는데 DB의 값도 암호화된 값이어야한다.
//		 * 만약 DB의 값이 암호화 된 상태가 아니라면 NoOpPasswordEncoder를 주입해주면 된다.
//		 * */
//		
//		auth.jdbcAuthentication().
//		dataSource(dataSource).
//		usersByUsernameQuery(query1).
//		rolePrefix("ROLE_").
//		authoritiesByUsernameQuery(query2);
//	}
//	
	
	/*
	 * 'remember-me' 쿠키의 생성은 기본적으로 username, 쿠키 만료시간, 패스워드를 base-64 방식으로 인코딩한 것인데
	 * 이러한 방식은 사용자가 패스워드를 변경하면 정상적인 값이라도 로그인이 될 수 없다는 단점이 있다.
	 * 이를 해결하기 위해 데이터베이스에 토큰을 보관할 수 있는 테이블을 생성해 처리하는 방식이 좋다.
	 * 데이터베이스를 이용하는 설정은 PersistentTokenRepository 인터페이스에 구현된 JdbcTokenRepositoryImpl 클래스를 이용한다.
	 * JdbcTokenRepositoryImpl 클래스는 기본적으로 DataSource가 필요하다.
	 * */
	private PersistentTokenRepository getJDBCRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		jdbcTokenRepositoryImpl.setDataSource(dataSource);
		return jdbcTokenRepositoryImpl;
	}
	
	/*
	 * [PasswordEncoder를 사용한 패스워드 암호화]
	 * 스프링 시큐리티 5버전 부터는 passwordEncoder(암호화)를 무조건적으로 설정해주어야 하는데 DB의 값도 암호화된 값이어야한다.
	 * PasswordEncoder를 적용하는 방식은 다음과 같이 구현한다. 
	 *  - 1. 구현클래스 작성
	 *  - 2. 시큐리티 설정 추가
	 *  - 3. 관련 컨트롤러나 서비스와 연동
	 * 
	 * 패스워드 저장시 강력한 암호화 방식은 단방향 해시 함수 방식으로 저장하는것이 가장 바람직하다.
	 * 여러 방식이 있지만 그중 가장 강력한 방식은  bcrypt 방식이다.
	 * 
	 * 다른 서비스에서도 이용되는 경우가 많기 때문에 별도의 빈으로 생성(@Bean으로 스프링 빈으로 컨테이너에 등록)될 수 있도록 처리한다.
	 * PasswordEncoder를 이용하기 위해서는 인증 매니저가 PasswordEncoder를 이용할 것이라는 것을 명시해야 한다.
	 * */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		log.info("build Auth global...");
		
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
}
