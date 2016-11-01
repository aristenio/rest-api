package br.com.test.restapi.domain;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team, String> {

	public List<Team> findByNameLike(String name);
	
}
