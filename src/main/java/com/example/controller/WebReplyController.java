package com.example.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.WebBoard;
import com.example.dto.WebReply;
import com.example.persistence.WebReplyRepository;

import lombok.extern.java.Log;

/*
 * 댓글 처리는 하나의 화면에서 이루어진다.
 * 따라서 이동이 없는 상태에서 데이터를 주고 받는 Ajax, WebSocket, Socket.io 등의 방식을 이용하는것이 좋다.
 * 여기서는 Ajax 방식을 이용하며 @RestController를 적용하여 데이터와 GET/POST/PUT/DELETE 와 같은 전송 방식을 이용한다.
 * 
 * 기존의 GET/POST 방식에서 벗어나서 최근에는 REST 방식을 이용하는데 REST 방식에서는 URL이 콘텐츠 자체를 의미하기 떄문에
 * 콘텐츠에 대한 어떤 작업을 할 것인지는 GET/POST/PUT/DELETE 등의 전송 방식(메소드)를 이용해서 처리한다.
 * 
 * REST 방식으로 설계하는 경우에는 명사가 URL의 구분이 되고 전송방식이 동사의 역할을 하게 된다.
 * 각 단계는 '/'를 이용해서 구분하고 마지막에는 정보(번호,아이디 등)를 사용한다.
 * !!![REST 방식에서 URL을 어떻게 설계할것이가 가장 중요하다.]!!!
 * 
 * REST 방식의 설계와 사용을 위해서 제공되는 어노테이션
 * - @RequestBody : 클라이언트가 보내는 JSON 데이터의 수집 및 가공
 * - @ResponseBody : 클라이언트에게 전송되는 데이터에 맞게 MIME 타입을 결정
 * - @PathVariable : URL의 경로에 포함된 정보 추출
 * - @RestController : 컨트롤러의 모든 메소드 리턴 타입으로 @ResponseBody를 기본으로 지정, 즉 RestController를 통해 리소스 자체를 리턴할 수 있다.
 * 					   @Controller를 지정하고 메소드위에 @ResponseBody를 추가해주어도 동일하다.
 * */

@RestController
@Log
@RequestMapping("/replies/*")
public class WebReplyController {
	
	@Autowired
	private WebReplyRepository webReplyRepository;
	
	// 게시물의 댓글 목록 조회
	private List<WebReply> getListByBoard(WebBoard board) throws RuntimeException {
		log.info("getListByBoard..." + board);
		return webReplyRepository.getRepliesOfBoard(board);
	}
	
	@GetMapping("/{bno}")
	public ResponseEntity<List<WebReply>> getReplies(@PathVariable("bno") Long bno) {
		log.info("get all replies.....");
		
		WebBoard board = new WebBoard();
		board.setBno(bno);
		
		return new ResponseEntity<>(getListByBoard(board),HttpStatus.OK);
	}
	
	/*
	 * save() 작업과 findBoard...()를 연속해서 호출하기 때문에 @Transactional 처리를 한다. 
	 * 지연로딩을 하면서 다른 엔티티의 값을 조회하고 싶다면 @Transactional을 이용해서 처리해야 한다.
	 * */
	@Transactional
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@PostMapping("/{bno}")
	public ResponseEntity<List<WebReply>> addReply(@PathVariable("bno") Long bno, @RequestBody WebReply reply) {
		log.info("addReply..............");
		log.info("BNO : " + bno);
		log.info("REPLY : " + reply);
		
		WebBoard board = new WebBoard();
		board.setBno(bno);
		
		reply.setBoard(board);
		webReplyRepository.save(reply);
		
		// ResponseEntity는 코드를 이용해서 직접 Http Response의 상태 코드(200, 404, 500등)와 데이터를 직접 제어해서 처리할 수 있다는 장점이 있다.
		// 첫번째 파라미터가 responseBody, 두번재 파라미터가 상태코드이다.
		return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED); // CREATED == 201 
	}
	
	@Transactional
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@DeleteMapping("/{bno}/{rno}")
	public ResponseEntity<List<WebReply>> remove(@PathVariable("bno") Long bno, @PathVariable("rno") Long rno) {
		log.info("delete reply :" + rno);
		
		webReplyRepository.deleteById(rno); // 댓글 삭제
		
		WebBoard board = new WebBoard();
		board.setBno(bno); // 현재 게시물 댓글 목록 반환
		
		return new ResponseEntity<>(getListByBoard(board), HttpStatus.OK);
	}
	
	// REST 방식에서 수정처리 과정은 POST 방식 대신 PUT 방식을 이용해서 처리한다.
	@Transactional
	@Secured(value = {"ROLE_BASIC", "ROLE_MANAGER", "ROLE_ADMIN"})
	@PutMapping("/{bno}")
	public ResponseEntity<List<WebReply>> modify(@PathVariable("bno") Long bno, @RequestBody WebReply reply) {
		log.info("modify reply : " + reply);
		
		webReplyRepository.findById(reply.getRno()).ifPresent(origin -> {
			origin.setReplyText(reply.getReplyText());
			webReplyRepository.save(origin);
		});
		
		WebBoard board = new WebBoard();
		board.setBno(bno);
		
		return new ResponseEntity<>(getListByBoard(board), HttpStatus.CREATED);
	}
	
}
