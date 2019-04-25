package com.example.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * 회원에 대한 클래스 생성시 일반적으로 User라는 용어를 사용하지만 User라는 타입은 이미 스프링 시큐리티에서 이용하고 있기 때문에 Member라는 이름을 이용한다.
 * username, password 등의 용어도 마찬가지의 이유로  uid, upw와 같은 식으로 이용한다.
 * 
 * @CreationTimestamp, @UpdateTimestamp 어노테이션과 LocalDateTime 타입을 사용할 경우  hibernate을 5.2.x 버전으로 사용해야한다.
 * MemberTests 클래스의 testInsert()를 실행하면 Member 엔티티와 MemberRole 엔티티를 동시에 저장하기 때문에 에러가 발생한다.
 * 이유는 엔티티들은 영속관계를 한 번에 처리하지 못하기 때문인데 이를 위해 cascade 설정을 추가해야 한다.
 * testRead()도 비슷한 이유로 둘 다 조회해야 하기 때문에 트랜잭션 처리 또는 즉시로딩(FetchType.EAGER)을 이용해서 조인을 하는 방식으로 처리해야 한다.
 * 
 * @OneToMany 어노테이션으로 '1대다' / '다대1' 설정 시 특정 엔티티를 영속상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶다면 영속성 전이 기능을 사용하면 된다.
 * 즉 영속성 전이라는 것은 연관관계를 맺은 엔티티들도 함께 준영속 혹은 비영속상태에서 영속상태로 만들어 CRUD 작업이 가능하도록 하는것이다.
 * 예로 부모 엔티티를 저장할때 연관된 자식 엔티티도 함께 저장할 수 있고 부모 엔티티를 삭제하면 연관된 자식 엔티티도 삭제할 수 있다.
	   [CascadeType 종류]
	   ALL  	모두 적용 
	   PERSIST  영속
	   MERGE	병합 
	   REMOVE	삭제 
	   REFRESH	REFRESH 
	   DETACH	DETACH 
 * */
@Getter
@Setter
@Entity
@Table(name = "tbl1_members")
@EqualsAndHashCode(of = "uId")
@ToString
public class Member {
	
	@Id
//	private String uid; // 어떤 이유 때문인지는 모르겠지만 칼럼명을 uid로 하려하면 테이블이 생성되지 않는다.
	private String uId;
	private String uPw;
	private String uName;
	
	@CreationTimestamp
	private LocalDateTime regdate;
	@UpdateTimestamp
	private LocalDateTime updatedate;
	
	// MemberRole은 정보에 대한 접근 방식 자체가 '회원'을 통한 접근이므로 별도의 연관관계를 설정하지 않는 단방향 방식으로 둔다.
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "member")
	private List<MemberRole> roles;
	
}
