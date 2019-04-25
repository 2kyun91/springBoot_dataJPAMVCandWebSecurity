package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dto.WebBoard;
import com.example.page.PageMaker;
import com.example.page.PageVO;
import com.example.persistence.CustomCrudRepository;
import com.example.persistence.WebBoardRepository;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/boards")
@Log
public class WebBoardController {
	
	/*
	 * @PageableDefault 어노테이션을 이용하면 간단하게 Pageable 타입의 객체를 생성할 수 있다.
	 * 하지만 페이지 번호가 0부터 시작한다는 점과 사용자가 임의로 값을 조절하여 보안에 취약하다는 단점들이 있다.
	 * 
	 * @PageableDefault를 이용하는 방식보다는 별도로 파라미터를 수집해서 처리하는 객체를 생성하는 방식(list2() 메소드)이 보다 좋은 방식이다.
	 * 브라우저에서 전달되는 파라미터들은 자동으로 PageVO로 처리된다.
	 * 이때 페이지 번호(page)와 페이지당 개수(size)가 처리되고 정렬방향과 정렬 대상의 칼럼은 컨트롤러에서 처리된다. 
	 * */
//	@GetMapping("/list")
//	public void list(@PageableDefault(direction = Sort.Direction.DESC, sort = "bno", size = 10, page = 0) Pageable page) {
//		log.info("list() called..." + page);
//	}
	
//	@GetMapping("/list")
//	public void list2(PageVO pageVO) {
//		Pageable pageable = pageVO.makePageable(0, "bno");
//		log.info("" + pageable);
//	}
	
	@Autowired
	private WebBoardRepository webBoardRepository;
	
	// 동적으로 JPQL 주입
	@Autowired
	private CustomCrudRepository customCrudRepository;
	
	/*
	 * 파라미터로 Model을 전달 받고 WebBoardRepository를 이용해서 페이지 처리를 진행한 결과를 result에 담는다.
	 * 검색 기능을 처리하기 위해서 Predicate를 생성하는 부분을 전달받은 PageVo를 이용하도록 해야한다.
	 * PageVO를 @ModelAttribute("PageVO")로 지정해두고 getType(), getKeyword()를 이용해서 Predicate를 처리하도록 한다.
	 * */
	@GetMapping("/list")
	public void list(@ModelAttribute("pageVO") PageVO vo, Model model) {
		Pageable page = vo.makePageable(0, "bno");
		
//		Page<WebBoard> result = webBoardRepository.findAll(webBoardRepository.makePredicate(null, null), page);
//		Page<WebBoard> result = webBoardRepository.findAll(webBoardRepository.makePredicate(vo.getType(), vo.getKeyword()), page);
		Page<Object[]> result = customCrudRepository.getCustomPage(vo.getType(), vo.getKeyword(), page);
		
		log.info("" + page);
		log.info("" + result);
		log.info("TOTAL PAGE NUMBER : " + result.getTotalPages());
		
		// Model에 직접 Page<T>를 담는 대신 PageMaker 객체를 담는다.
		model.addAttribute("result", new PageMaker(result));
	}
	
	@GetMapping("/register")
	public void registerGET(@ModelAttribute("vo") WebBoard vo) {
		log.info("register get");
	}
	
	@PostMapping("/register")
	public String registerPOST(@ModelAttribute("vo") WebBoard vo, RedirectAttributes rttr) {
		log.info("register post");
		log.info("" + vo);
		
		webBoardRepository.save(vo);
		/*
		 * addFlashAttribute()는 RedirectAttributes가 제공하는 함수로 리다이렉트 직전에 플래시에 저장하는 메소드이고 리다이렉트 이후에는 소멸된다.
		 * RedirectAttributes는 리다이렉트 시 헤더에 파라미터를 붙이지 않기 떄문에 URL에 정보가 노출되지 않는다.
		 * */
		rttr.addFlashAttribute("msg", "success");
		
		return "redirect:/boards/list";
	}
	
	@GetMapping("/view")
	public void view(Long bno, @ModelAttribute("pageVO") PageVO vo, Model model) {
		log.info("BNO : " + bno);
		
		webBoardRepository.findById(bno).ifPresent(board -> model.addAttribute("vo" ,board));
	}
	
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@GetMapping("/modify")
	public void modify(Long bno, @ModelAttribute("pageVO") PageVO vo, Model model) {
		log.info("MODIFY BNO : " + bno);
		
		webBoardRepository.findById(bno).ifPresent(board -> model.addAttribute("vo", board));
	}
	
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@PostMapping("/modify")
	public String modifyPost(WebBoard board, PageVO vo, RedirectAttributes rttr) {
		log.info("MODIFY WebBoard : " + board);
		
		webBoardRepository.findById(board.getBno()).ifPresent(origin -> {
			// 기존 게시물의 내용에 변경되는 내용을 갱신한다.
			origin.setTitle(board.getTitle());
			origin.setContent(board.getContent());
			
			// DB에 저장
			webBoardRepository.save(origin);
			
			rttr.addFlashAttribute("msg", "success");
			rttr.addAttribute("bno", origin.getBno());
		});
		
		rttr.addAttribute("page", vo.getPage());
		rttr.addAttribute("size", vo.getSize());
		rttr.addAttribute("type", vo.getType());
		rttr.addAttribute("keyword", vo.getKeyword());
		
		return "redirect:/boards/list";
	}
	
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@PostMapping("/delete")
	public String delete(Long bno, PageVO vo, RedirectAttributes rttr) {
		log.info("DELETE BNO : " + bno);
		
		webBoardRepository.deleteById(bno);
		
		rttr.addFlashAttribute("msg", "success");
		
		// 페이징과 검색했던 결과로 이동하는 경우. 
		// addAttribute()는 URL에 파라미터를 추가해서 전송한다. 
		rttr.addAttribute("page", vo.getPage());
		rttr.addAttribute("size", vo.getSize());
		rttr.addAttribute("type", vo.getType());
		rttr.addAttribute("keyword", vo.getKeyword());
		
		return "redirect:/boards/list";
	}
}
