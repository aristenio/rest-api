package br.com.test.restapi.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Team extends AbstractBean {

	@Indexed(unique = true)
	private String name;

	public Team() {
	}

	public Team(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "{\"id\":\"" + this.id + "\",\"name\":\"" + this.name + "\"}";
	}

}
