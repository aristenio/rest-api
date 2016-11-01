package br.com.test.restapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.Team;
import br.com.test.restapi.domain.TeamMember;
import br.com.test.restapi.domain.TeamMemberRepository;

@Component
public class TeamMemberService {
	
	@Autowired
	private TeamMemberRepository repository;
	
	public TeamMember add(Team team, Member member) {
		return repository.insert(new TeamMember(team.getId(), member.getId()));
	}
	
	public void remove(String id) {
		TeamMember teamMember = repository.findByMemberId(id);
		repository.delete(teamMember);
	}

	public List<TeamMember> findByTeamId(String id) {
		return repository.findByTeamId(id);
	}

	public TeamMember findByMemberId(String id) {
		return repository.findByMemberId(id);
	}

}
