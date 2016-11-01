package br.com.test.restapi.domain;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface TeamMemberRepository extends MongoRepository<TeamMember, String>{
	
	public List<TeamMember> findByTeamId(String id);
	
	public TeamMember findByMemberId(String id);
}
