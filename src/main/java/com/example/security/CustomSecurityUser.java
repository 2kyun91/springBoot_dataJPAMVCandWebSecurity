package com.example.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.dto.Member;
import com.example.dto.MemberRole;

import lombok.Getter;
import lombok.Setter;

/*
 * [MemberRepositroy 연동 작업 처리]
 * CustomUserDetailsService 클래스에서 MemberRepository를 @Autowired 어노테이션으로 주입한다. 
 * findById()를 사용해 Member 엔티티 인스턴스를 얻어와 리턴 타입 UserDetails에 맞게 수정한다.
 * 별도의 클래스(CustomSecurityUser)를 만들어 Member 인스턴스를 감싸는 형태의 클래스를 만든다.
 * 이 방법을 이용하면 Member의 모든 정보를 추가적으로 사용해야하는 상황에 유용하다.
 * 
 * 스프링 시큐리티의 User 클래스를 상속받는 형태로 생성하고 생성자를 호출하는 코드를 호출한다.
 * User 클래스는 UserDetails를 구현해 둔 것이다.
 * */
@Getter
@Setter
public class CustomSecurityUser extends User{
	
	private static final String ROLE_PREFIX = "ROLE_";
	
	private Member member;
	
	/*
	 * User 클래스 default 생성자 
	 * public CustomSecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
	 * 	super(username, password, authorities); 
	 * }
	 */
	
	public CustomSecurityUser(Member member) { 
		// 상위 클래스(User)생성자를 호출해 Member 인스턴스를 감싼다.
		super(member.getUId(), member.getUPw(), makeGrantedAuthority(member.getRoles()));
		this.member = member;
	}
	
	private static List<GrantedAuthority> makeGrantedAuthority(List<MemberRole> roles) {
		List<GrantedAuthority> list = new ArrayList<>();
		
		roles.forEach(
				role -> list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getRoleName()))
		);
		
		return list;
	}

}
