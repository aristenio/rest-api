package br.com.test.restapi.domain;

import org.springframework.data.annotation.Id;

public class AbstractBean {
	
	@Id
	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
