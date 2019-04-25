package com.example.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.java.Log;

@Getter
@ToString(exclude = "pageList")
@Log
public class PageMaker<T> {
	/*
	 * prevPage : 페이지 목록의 맨앞으로 이동하는데 필요한 정보
	 * nextPage : 페이지 목록의 맨뒤로 이동하는데 필요한 정보
	 * currentPage : 현재 페이지 정보
	 * pageList : 페이지 번호의 시작부터 끝까지 저장한 리스트
	 * currentPageNum : 화면에 보이는 1부터 시작하는 페이지 번호
	 * */
	
	private Page<T> result;
	
	private Pageable prevPage;
	private Pageable nextPage;
	
	private int currentPageNum;
	private int totalPageNum;
	
	private Pageable currentPage;
	private List<Pageable> pageList;
	
	// 페이징 객체 생성자
	public PageMaker(Page<T> result) {
		this.result = result;
		this.currentPage = result.getPageable();
		this.currentPageNum = currentPage.getPageNumber() + 1;
		this.totalPageNum = result.getTotalPages();
		this.pageList = new ArrayList<>();
		calcPages();
	}
	
	public void calcPages() {
		int tempEndNum = (int) (Math.ceil(this.currentPageNum/10.0)*10);
		int startNum = tempEndNum - 9;
		
		Pageable startPage = this.currentPage;
		
		// 시작 페이지로 이동하는 경우
		for (int i = startNum; i < this.currentPageNum; i++) {
			startPage = startPage.previousOrFirst();
		}
		
		this.prevPage = startPage.getPageNumber() <= 0 ? null : startPage.previousOrFirst();
		
		log.info("tempEndNum : " + tempEndNum);
		log.info("total : " + totalPageNum);
		
		if (this.totalPageNum < tempEndNum) {
			tempEndNum = this.totalPageNum;
			this.nextPage = null;
		}
		
		for (int i = startNum; i <= tempEndNum; i++) {
			pageList.add(startPage);
			startPage = startPage.next();
		}
		
		this.nextPage = startPage.getPageNumber() + 1 < totalPageNum ? startPage : null;
	}
}
