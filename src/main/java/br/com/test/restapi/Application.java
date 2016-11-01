package br.com.test.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.test.restapi.domain.Account;
import br.com.test.restapi.domain.AccountRepository;
import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.MemberRepository;
import br.com.test.restapi.domain.Team;
import br.com.test.restapi.domain.TeamMember;
import br.com.test.restapi.domain.TeamMemberRepository;
import br.com.test.restapi.domain.TeamRepository;

@SpringBootApplication
@RestController
public class Application implements CommandLineRunner {

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private TeamMemberRepository teamMemberRepository; 

	@RequestMapping("/version")
	@ResponseBody
	public String version() {
		return "0.1";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		teamRepository.deleteAll();
		memberRepository.deleteAll();
		teamMemberRepository.deleteAll();

		Team teamGama = teamRepository.save(new Team("Gama"));
		Team teamBeta = teamRepository.save(new Team("Beta"));
		Team teamAlpha = teamRepository.save(new Team("Alpha"));

		Member memberFulano = memberRepository.save(new Member("Fulano"));
		Member memberJose = memberRepository.save(new Member("Jose"));
		Member memberBeltrano = memberRepository.save(new Member("Beltrano"));
		Member memberJoaquim = memberRepository.save(new Member("Joaquim"));
		Member memberSicrano = memberRepository.save(new Member("Sicrano"));
		Member memberXavier = memberRepository.save(new Member("Xavier"));
		
		teamMemberRepository.insert(new TeamMember(teamAlpha.getId(), memberFulano.getId()));
		teamMemberRepository.insert(new TeamMember(teamAlpha.getId(), memberSicrano.getId()));
		teamMemberRepository.insert(new TeamMember(teamBeta.getId(), memberBeltrano.getId()));
	}

	@Bean
	CommandLineRunner init(final AccountRepository accountRepository) {

		return new CommandLineRunner() {

			@Override
			public void run(String... arg0) throws Exception {
				accountRepository.deleteAll();
				accountRepository.save(new Account("user", "pass"));

			}

		};

	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	AccountRepository accountRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Account account = accountRepository.findByUsername(username);
				if (account != null) {
					return new User(account.getUsername(), account.getPassword(), true, true, true, true,
							AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("could not find the user '" + username + "'");
				}
			}

		};
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/version", "/info", "/health").permitAll()
								.anyRequest().fullyAuthenticated()
								.and().httpBasic()
								.and().csrf()
								.disable();
	}

}
