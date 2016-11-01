package br.com.test.restapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.Team;
import br.com.test.restapi.domain.TeamMember;
import br.com.test.restapi.domain.TeamRepository;
import br.com.test.restapi.exceptions.MemberIsNotInTeamException;
import br.com.test.restapi.exceptions.MemberJustInTeamException;
import br.com.test.restapi.exceptions.MemberNotFoundException;
import br.com.test.restapi.exceptions.TeamNotFoundException;

@Component
public class TeamService extends AbstractService<TeamRepository, Team> {

	@Autowired
	private MemberService memberService;

	@Autowired
	private TeamMemberService teamMemberService;

	public List<Team> searchTeamByName(String name) {
		return repository.findByNameLike(name);
	}

	public List<Member> getTeamMembers(String id) {
		Team team = findById(id);
		List<Member> members = new ArrayList<>();

		if (null != team) {
			List<TeamMember> teamMembers = teamMemberService.findByTeamId(id);

			for (TeamMember teamMember : teamMembers) {
				Member member = memberService.findById(teamMember.getMemberId());
				members.add(member);
			}
		}

		return members;
	}

	public void addTeamMember(Team team, Member member)
			throws TeamNotFoundException, MemberNotFoundException, MemberJustInTeamException {
		TeamMember teamMember = teamMemberService.findByMemberId(member.getId());
		Team localTeam = findById(team.getId());
		Member localMember = memberService.findById(member.getId());

		if (null == localTeam) {
			throw new TeamNotFoundException();
		} else if (null == localMember) {
			throw new MemberNotFoundException();
		} else if (null != teamMember) {
			throw new MemberJustInTeamException();
		} else {
			teamMemberService.add(localTeam, localMember);
		}
	}

	public void removeTeamMember(String idTeam, String idMember)
			throws TeamNotFoundException, MemberNotFoundException, MemberIsNotInTeamException {
		TeamMember teamMember = teamMemberService.findByMemberId(idMember);
		Team localTeam = findById(idTeam);
		Member localMember = memberService.findById(idMember);

		if (null == localTeam) {
			throw new TeamNotFoundException();
		} else if (null == localMember) {
			throw new MemberNotFoundException();
		} else if (null != teamMember) {
			throw new MemberIsNotInTeamException();
		} else {
			teamMemberService.remove(localMember.getId());
		}

	}

}