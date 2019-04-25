package com.example.persistence;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.example.dto.QWebBoard;
import com.example.dto.WebBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

/*
 * 게시물의 검색에는 동적 쿼리를 이용해서 처리한다.
 * pom.xml에 Querydsl 관련 라이브러리와 코드 생성 플러그인을 추가한다.
 * Qdomain을 생성하는 코드 생성 플러그인 역시 추가한다.
 * Qdomain은 target/generated-sources/java 폴더에 클래스로 생성된다.
 * 
 * 검색 기능을 구현할 때 Qdomain을 사용할 수 있도록 WebBoardRepository에 QueryDsl 인터페이스를 추가한다(QuerydslPredicateExecutor<>)
 * 
 * 페이징 구현은 QuerydslPredicateExecutor의 findAll()과 Pageable을 사용한다.
 * QuerydslPredicateExecutor의 findAll()은 Predicate 타입의 파라미터와 Pageable을 파라미터로 전달받는다. 
 * Predicate 타입의 파라미터는 QWebBoard를 이용해서 작성한다.
 * */
public interface WebBoardRepository extends CrudRepository<WebBoard, Long>, QuerydslPredicateExecutor<WebBoard>{
	
	// 검색에 필요한 타입과 키워드를 이용해서 쿼리를 생성한다.
	public default Predicate makePredicate(String type, String keyword) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();
		
		QWebBoard qWebBoard = QWebBoard.webBoard;
		
		// type if ~ else
		
		// bno > 0
		booleanBuilder.and(qWebBoard.bno.gt(0));
		
		if (type == null) {
			return booleanBuilder;
		}
		
		switch (type) {
		case "a" :
			booleanBuilder.andAnyOf(qWebBoard.title.like("%" + keyword + "%"), qWebBoard.content.like("%" + keyword + "%"), qWebBoard.writer.like("%" + keyword + "%"));
			break;
		case "t" :
			booleanBuilder.and(qWebBoard.title.like("%" + keyword + "%"));
			break;
		case "c" :
			booleanBuilder.and(qWebBoard.content.like("%" + keyword + "%"));
			break;
		case "w" :
			booleanBuilder.and(qWebBoard.writer.like("%" + keyword + "%"));
			break;
		}
		
		return booleanBuilder;
	}
	
}
