package com.example.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * MemberRole은 그 자체로는 별다른 의미를 가지지 못하고 Member에 대한 정보의 라이프사이클과 강하게 묶여 있기 때문에 Member가 MemberRole에 대한 참조를 갖도록 한다.
 * */
@Getter
@Setter
@Entity
@Table(name = "tbl1_member_roles")
@EqualsAndHashCode(of = "fno")
@ToString
public class MemberRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_memberroles")
	@SequenceGenerator(name = "seq_memberroles", sequenceName = "SEQ_MEMBERROLES", allocationSize = 1, initialValue = 1)
	private Long fno;
	private String roleName;
	
}
