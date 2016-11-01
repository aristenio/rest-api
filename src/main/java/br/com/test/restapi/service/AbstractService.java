package br.com.test.restapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.test.restapi.domain.AbstractBean;

public class AbstractService<T extends MongoRepository<B, String>, B extends AbstractBean> {
	
	@Autowired
	protected T repository;
	
	public B findById(String id) {
		return repository.findOne(id);
	}
	
	public List<?> findAll() {
		return repository.findAll();
	}

	public void delete(String id) {
		repository.delete(id);
	}

	public void add(B bean) {
		repository.insert(bean);
	}
	
	public B update(B bean) {
		return repository.save(bean);
	}
}
