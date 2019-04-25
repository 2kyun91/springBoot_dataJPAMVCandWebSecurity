package com.example.dto;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "tbl1_webreplies")
@EqualsAndHashCode(of = "rno")
@ToString(exclude = "board")
public class WebReply {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_webreplies")
	@SequenceGenerator(name = "seq_webreplies", sequenceName = "SEQ_WEBREPLIES", allocationSize = 1, initialValue = 1)
	private Long rno;
	private String replyText;
	private String replyer;
	
	@CreationTimestamp
	private Timestamp regdate;
	@UpdateTimestamp
	private Timestamp updatedate;
	
	/*
	 * REST 방식에서는 데이터를 전송하거나 반환할 때 JSON 형태의 데이터를 주고 받도록 설계한다.
	 * JSON 변환은 현재 객체를 JSON으로 반환하는거 외에도 객체가 참조하고 있는 내부 객체 역시 JSON으로 변환한다.
	 * 양뱡향의 경우 이러한 변환이 상호 호출되기 때문에 무한 반복해서 생성하는 문제가 생길수 있기 때문에 @JsonIgnore을 적용한다.
	 * 
	 *  WebReply 객체를 변환한 JSON 데이터에서는 WebBoard와 관련된 내용은 제외된다.
	 * */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private WebBoard board;
}
