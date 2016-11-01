package br.com.test.restapi.exceptions;

public class MemberJustInTeamException extends Exception {

	private static final long serialVersionUID = -2178886985178567043L;

	public MemberJustInTeamException() {
		super("Member just in a team");
	}

}
