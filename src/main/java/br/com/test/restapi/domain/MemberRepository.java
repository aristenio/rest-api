package br.com.test.restapi.domain;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member, String> {

	public List<Member> findByNameLike(String name);

}
