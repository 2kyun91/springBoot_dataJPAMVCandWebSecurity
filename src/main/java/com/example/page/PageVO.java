package com.example.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageVO {
	private static final int DEFAULT_SIZE = 10;
	private static final int DEFAULT_MAX_SIZE = 50;
	
	private int page;
	private int size;
	
	/* 
	 * 검색조건의 처리가 가능하도록 하기 위해 검색 keyword와 검색 type을 수집할 수 있도록 인스턴스 변수와 getter/setter를 추가한다.
	 * */ 
	private String keyword;
	private String type;
	
	public PageVO(String keyword, String type) {
		this.page = 1;
		this.size = DEFAULT_SIZE;
	}
	

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page < 0 ? 1 : page;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
	}
	
	/* 전달되는 파라미터를 이용해서 최종적으로 PageRequest로 Pageable 객체를 만들어낸다.
	 * 브라우저에서 전달되는 page 값을 1 줄여서 Pageable 타입을 생성한다.
	 */
	public Pageable makePageable(int direction, String... props) {
		Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(this.page - 1, this.size, dir, props);
	}
}
