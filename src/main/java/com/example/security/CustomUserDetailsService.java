package com.example.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.persistence.MemberRepository;

import lombok.extern.java.Log;

/*
 * 2) 기존에 작성된 Repository나 서비스 객체들을 이용해서 별도로 시큐리티 관련 서비스를 개발하는 방법(사용자 정의)
 * 모든 인증 매니저는 결과적으로 UserDetails 타입의 객체를 반환하도록 구현된다.
 * 만약 인증 매니저를 커스터마이징하려면 새로운 클래스를 정의해 UserDetailsService 인터페이스를 구현하고 이를 HttpSecurity 객체가 사용할 수 있도록 지정하면 된다.
 * 
 * UserDetailsService의 loadUserByUsername()은 사용자의 계정 정보(아이디)를 이용해서 UserDetails 인터페이스를 구현한 객체를 반환해야 한다.
 * */
@Service // @Service 어노테이션을 추가했으므로 스프링에서 빈으로 처리한다(SecurityConfig 클래스에서 @Autowired 어노테이션으로 주입된다.).
@Log
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		/* 
		 * User 클래스는 UserDetails를 구현해 둔 것으로 username, password, Authority(권한)를 이용하는 생성자를 제공한다.
		 * User 클래의 인스턴스는 Collection<Authority>를 가질수 있으므로 Arrays.asList()를 이용해서 여러 권한을 부여할 수 있다.
		 * 
		 * SimpleGrantedAuthority 클래스는 GrantedAuthority라는 인터페이스의 구현체로 문자열을 반환하는 getAuthority() 메소드 하나만 갖고 있다.
		 * */
//		User sampleUser = new User(username, "1111", Arrays.asList(new SimpleGrantedAuthority("ROLE_MANAGER")));
//		return sampleUser;
		
//		memberRepository.findById(username).ifPresent(member -> log.info("" + member));

		return memberRepository.findById(username).filter(m -> m != null).map(m -> new CustomSecurityUser(m)).get();
	}
	
}
