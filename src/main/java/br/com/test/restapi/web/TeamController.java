package br.com.test.restapi.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.Team;
import br.com.test.restapi.service.TeamService;

@RestController
@RequestMapping(value = "/teams")
public class TeamController extends AbstractController<TeamService, Team> {

	@RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
	public ResponseEntity<List<Team>> searchTeamByName(@PathVariable String name) {
		List<Team> teams = service.searchTeamByName(name);

		if (teams == null || teams.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Team>>(teams, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
	public ResponseEntity<List<Member>> getTeamMembers(@PathVariable String id) {
		List<Member> members = service.getTeamMembers(id);

		if (members == null || members.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<List<Member>>(members, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> addTeamMember(@PathVariable String id, @RequestBody Member member) {
		Team team = service.findById(id);

		if (team == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		try {
			service.addTeamMember(team, member);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{idTeam}/{idMember}", method = RequestMethod.POST)
	public ResponseEntity<?> delTeamMember(@PathVariable("idTeam") String idTeam,
			@PathVariable("idMember") String idMember) {

		try {
			service.removeTeamMember(idTeam, idMember);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
