package com.example.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.Member;
import com.example.persistence.MemberRepository;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/member/")
public class MemberController {
	
	@Autowired // SecurityConfigure 클래스에서 @Bean으로 스프링 빈 컨테이너에 등록하였기 때문에 @Autowired로 주입할 수 있다.
	PasswordEncoder passwordEncoder;
	
	@Autowired
	MemberRepository memberRepository; // 데이터베이스 처리를 위해 추가한다.
	
	@GetMapping("/join")
	public void join() {
		
	}
	
	@Transactional
	@PostMapping("/join")
	public String joinPost(@ModelAttribute("member")Member member) {
		log.info("MEMBER : " + member);
		
		String encryptPw = passwordEncoder.encode(member.getUPw());
		
		log.info("en : " + encryptPw);
		
		member.setUPw(encryptPw);
		
		memberRepository.save(member);
		
		return "/member/joinResult";
	}
}
