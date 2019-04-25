package com.example.persistence;

import org.springframework.data.repository.CrudRepository;

import com.example.dto.WebBoard;

/*
 * 동적으로 JPQL을 처리(사용자 정의 쿼리)하기 위한 고려 사항
 *  - 원하는 기능을 별도의 사용자 정의 인터페이스로 설계한다. / CustomCrudRepository
 *  - 엔티티의 Repository 인터페이스를 설계할 때 사용자 정의 인터페이스 역시 같이 상속하도록 설계한다. / CustomWebBoard
 *  - 엔티티 Repository를 구현하는 클래스를 생성한다. / CustomCrudRepositoryImpl
 *  - 이때 반드시 'Repository 이름' + 'Impl'로 클래스 이름을 지정한다.
 *  - 클래스 생성시 부모 클래스를 QuerydslRepositorySupport로 지정한다. / extends QuerydslRepositorySupport
 *  - Repository 인터페이스 Impl 클래스에 JPQLQuery 객체를 이용해서 내용을 작성한다.
 *  - CustomCrudRepositoryImpl -> CustomWebBoard -> CustomCrudRepository
 * */
public interface CustomCrudRepository extends CrudRepository<WebBoard, Long>, CustomWebBoard{

}
