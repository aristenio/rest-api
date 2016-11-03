package br.com.test.restapi.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TeamMember extends AbstractBean{
	
	private String teamId;

	private String memberId;

	public TeamMember(String teamId, String memberId) {
		this.teamId = teamId;
		this.memberId = memberId;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
}
