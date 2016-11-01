package br.com.test.restapi.exceptions;

public class MemberNotFoundException extends Exception {

	private static final long serialVersionUID = 833099638081227349L;

	public MemberNotFoundException() {
		super("Member not exists");
	}

}
