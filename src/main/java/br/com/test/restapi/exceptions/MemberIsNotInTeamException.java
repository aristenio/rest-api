package br.com.test.restapi.exceptions;

public class MemberIsNotInTeamException extends Exception {

	private static final long serialVersionUID = -6908444556705816820L;

	public MemberIsNotInTeamException() {
		super("Member isn't in the team");
	}

}
