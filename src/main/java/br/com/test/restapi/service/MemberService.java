package br.com.test.restapi.service;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.MemberRepository;

@Component
public class MemberService extends AbstractService<MemberRepository, Member>{
	
	public List<Member> searchTeamByName(String name) {
		return repository.findByNameLike(name);
	}
}
