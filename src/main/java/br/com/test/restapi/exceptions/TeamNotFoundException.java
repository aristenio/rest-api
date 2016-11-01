package br.com.test.restapi.exceptions;

public class TeamNotFoundException extends Exception {

	private static final long serialVersionUID = -4896259929287169642L;

	public TeamNotFoundException() {
		super("Team not exists");
	}

}
