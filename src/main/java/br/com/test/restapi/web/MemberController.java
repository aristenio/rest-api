package br.com.test.restapi.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.service.MemberService;

@RestController
@RequestMapping(value = "/members")
public class MemberController extends AbstractController<MemberService, Member>{

}
