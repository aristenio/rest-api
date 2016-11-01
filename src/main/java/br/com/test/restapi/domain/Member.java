package br.com.test.restapi.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Member extends AbstractBean {

	@Indexed(unique = true)
	private String name;
	
	public Member() {}
	
	public Member(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
