package br.com.test.restapi.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.service.MemberService;

@RestController
@RequestMapping(value = "/members")
public class MemberController extends AbstractController<MemberService, Member>{
	
	@RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
	public ResponseEntity<List<Member>> searchTeamByName(@PathVariable String name) {
		List<Member> teams = service.searchTeamByName(name);

		if (teams == null || teams.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Member>>(teams, HttpStatus.OK);
	}

}
