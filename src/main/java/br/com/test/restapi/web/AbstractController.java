package br.com.test.restapi.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.test.restapi.domain.AbstractBean;
import br.com.test.restapi.service.AbstractService;

public class AbstractController<T extends AbstractService, B extends AbstractBean> {

	@Autowired
	protected T service;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<B> get(@PathVariable String id) {
		B bean = (B) service.findById(id);

		if (bean == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<B>(bean, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<B>> getAll() {
		return new ResponseEntity<List<B>>(service.findAll(), HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> add(@RequestBody B bean) {
		service.add(bean);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@RequestBody B bean) {

		B newBean = (B) service.update(bean);

		if (newBean == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable String id) {

		service.delete(id);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
