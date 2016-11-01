package br.com.test.restapi.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AccountRepository extends MongoRepository<Account, String> {
  
  public Account findByUsername(String username);

}
