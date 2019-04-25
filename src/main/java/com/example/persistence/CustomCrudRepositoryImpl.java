package com.example.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.example.dto.QWebBoard;
import com.example.dto.QWebReply;
import com.example.dto.WebBoard;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.hibernate.AbstractHibernateQuery;

import lombok.extern.java.Log;

@Log
public class CustomCrudRepositoryImpl extends QuerydslRepositorySupport implements CustomWebBoard{ // QuerydslRepositorySupport 클래스는 생성자를 구현한다.
	
	public CustomCrudRepositoryImpl() {
		super(WebBoard.class);
	}

	@Override
	public Page<Object[]> getCustomPage(String type, String keyword, Pageable page) {
		log.info("====================");
		log.info("TYPE : " + type);
		log.info("KEYWORD : " + keyword);
		log.info("PAGE : " + page);
		log.info("====================");
		
		QWebBoard qWebBoard = QWebBoard.webBoard;
		QWebReply qWebReply = QWebReply.webReply;
		 
		/*
		  * 페이징 처리
		  * 페이징 처리 시 Querydsl의 Qdomain 등을 이용할 수 있다.
		  * Tuple에는 where(), orderBy()등의 기능을 이용해서 원하는 조건을 제어할 수 있다.
		 */
		JPQLQuery<WebBoard> query = from(qWebBoard);
		JPQLQuery<Tuple> tuple = query.select(qWebBoard.bno, qWebBoard.title, qWebReply.count(), qWebBoard.writer, qWebBoard.regdate);
		
		tuple.leftJoin(qWebReply);
		tuple.on(qWebBoard.bno.eq(qWebReply.board.bno));
		tuple.where(qWebBoard.bno.gt(0L));
		
		if (type != null) {
			switch (type.toLowerCase()) {
			case "t":
				tuple.where(qWebBoard.title.like("%" + keyword + "%"));
				break;
			case "c":
				tuple.where(qWebBoard.content.like("%" + keyword + "%"));
				break;
			case "w":
				tuple.where(qWebBoard.writer.like("%" + keyword + "%"));
				break;
			}
		};
		
		tuple.groupBy(qWebBoard.bno, qWebBoard.title, qWebBoard.writer, qWebBoard.regdate);
		tuple.orderBy(qWebBoard.bno.desc());
		
		List<Tuple> selectAllList = tuple.fetch();
		
		tuple.offset(page.getOffset());
		tuple.limit(page.getPageSize());
		
		List<Tuple> list = tuple.fetch(); // tuple.fetch()의 결과를 Collection에 담는다.
		List<Object[]> resultList = new ArrayList<>();
		
		list.forEach(t -> { // Object[] 형식으로 처리한다.
			resultList.add(t.toArray());
		});
		
//		long total = tuple.fetchCount(); // fetchCount() 함수가 버전 4.x.x에서 오류를 발생하는 버그가 있다고 한다, 67라인으로 대체한다.
		long total = selectAllList.size();
		
		return new PageImpl<>(resultList, page, total);
	}
	
}
