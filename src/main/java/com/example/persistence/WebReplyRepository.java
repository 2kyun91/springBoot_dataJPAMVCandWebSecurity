package com.example.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.dto.WebBoard;
import com.example.dto.WebReply;

public interface WebReplyRepository extends CrudRepository<WebReply, Long>{
	/*
	 * @Query의 가장 큰 단점.
	 * 게시물 조회 시 각 게시물의 댓글수를 출력하는 경우 게시물의 수만큼 tbl1_webreplies 테이블을 조회한다.
	 * 흔히 'N+1 검색'이라고 하며 이는 성능의 문제를 일으킨다.
	 * 만약 게시물의 수가 10만건인 경우 tbl1_webreplies 테이블을 조회하는 횟수도 기하급수적으로 늘어난다.
	 * @Query의 가장 큰 한계는 JPQL의 내용이 고정된다는 점이다.
	 * 조회조건이 있는 경우 그 상황에 맞게 JPQL이 실행되어야 하는데 @Query는 프로젝트 로딩 시점에서 적합성을 검사하기 때문에 쉽지않다. 
	 * 
	 * 원하는 시점에 원하는 JPQL을 생성해서 처리하고 싶다면 사용자가 직접 Repository를 조절하는 방식으로 이용해야한다.(동적으로 JPQL을 처리)
	 * */
	@Query(value = "select * from tbl1_webreplies r where r.board_bno = ?1 and r.rno > 0 order by r.rno asc", nativeQuery = true)
	public List<WebReply> getRepliesOfBoard(WebBoard board);
}
