package br.com.test.restapi;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.test.restapi.domain.Member;
import br.com.test.restapi.domain.Team;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "management.port=0" })
@ContextConfiguration
public class ApplicationTests {

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private HttpEntity<String> requestDefault;

	private HttpHeaders headers;

	@Before
	public void setUp() {

		String plainCreds = "user:pass";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		requestDefault = new HttpEntity<String>(headers);
	}

	@Test
	public void shouldReturn200WhenSendingRequestToControllerWithoutAuth() throws Exception {
		ResponseEntity<String> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/version",
				String.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void shouldReturn200WhenSendingRequestToManagementEndpoint() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = this.testRestTemplate.getForEntity("http://localhost:" + this.mgt + "/info",
				Map.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		@SuppressWarnings("rawtypes")
		ResponseEntity<List> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/",
				HttpMethod.GET, requestDefault, List.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void shouldReturn401WhenSendingUnautorizedRequestToController() throws Exception {

		ResponseEntity<Void> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/",
				HttpMethod.GET, null, Void.class);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void shouldReturnThreeInitialTeams() throws Exception {
		ParameterizedTypeReference<List<Team>> typeRef = new ParameterizedTypeReference<List<Team>>() {
		};

		ResponseEntity<List<Team>> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/",
				HttpMethod.GET, requestDefault, typeRef);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<Team> teams = entity.getBody();

		then(teams.size()).isGreaterThanOrEqualTo(3);

		Team teamGama = teams.get(0);
		then(teamGama.getName()).startsWith("Gama");

		Team teamBeta = teams.get(1);
		then(teamBeta.getName()).startsWith("Beta");

		Team teamAlpha = teams.get(2);
		then(teamAlpha.getName()).startsWith("Alpha");
	}

	@Test
	public void shouldReturnThreeInitialTeamsOrderingByName() throws Exception {
		RestTemplate restTemplate = restTemplate();
		String url = "http://localhost:" + this.port + "/teams?sort=name";

		ResponseEntity<PagedResources<Team>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestDefault,
				new ParameterizedTypeReference<PagedResources<Team>>() {
				}, port, 0, 100);
		PagedResources<Team> resources = responseEntity.getBody();
		List<Team> teams = new ArrayList<>(resources.getContent());

		Team teamAlpha = teams.get(0);
		then(teamAlpha.getName()).startsWith("Alpha");

		Team teamBeta = teams.get(1);
		then(teamBeta.getName()).startsWith("Beta");

		Team teamGama = teams.get(2);
		then(teamGama.getName()).startsWith("Gama");

	}

	@Test
	public void shouldReturnThreeInitialTeamsPaging() {
		RestTemplate restTemplate = restTemplate();
		String url = "http://localhost:" + this.port + "/teams?size=2&page=0&sort=name";

		ResponseEntity<PagedResources<Team>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestDefault,
				new ParameterizedTypeReference<PagedResources<Team>>() {
				}, port, 0, 100);
		PagedResources<Team> resources = responseEntity.getBody();
		List<Team> teams = new ArrayList<>(resources.getContent());

		then(teams.size()).isEqualTo(2);

		Team teamAlpha = teams.get(0);
		then(teamAlpha.getName()).isEqualTo("Alpha");

		Team teamBeta = teams.get(1);
		then(teamBeta.getName()).isEqualTo("Beta");
	}
	
	@Test
	public void searchTeam() {
		ParameterizedTypeReference<List<Team>> typeRef = new ParameterizedTypeReference<List<Team>>() {
		};

		ResponseEntity<List<Team>> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/Alp",
				HttpMethod.GET, requestDefault, typeRef);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<Team> teams = entity.getBody();

		then(teams.size()).isEqualTo(1);

		Team team = teams.get(0);
		then(team.getId()).isNotEmpty();
		then(team.getName()).isEqualTo("Alpha");
	}

	@Test
	public void insertNewTeam() throws Exception {
		Team team = new Team("teste");

		RestTemplate restTemplate = restTemplate();
		String url = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestEntity = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entity = restTemplate.postForEntity(url, requestEntity, Team.class);
		
		then(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	public void updateTeam() throws Exception {
		ParameterizedTypeReference<List<Team>> typeRef = new ParameterizedTypeReference<List<Team>>() {
		};
		
		ResponseEntity<List<Team>> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/Ga",
				HttpMethod.GET, requestDefault, typeRef);
		List<Team> teams = entity.getBody();
		Team team = teams.get(0);
		team.setName("Gama2");
		
		RestTemplate restTemplate = restTemplate();
		String url = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestEntity = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entityUpdate = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Team.class);
		
		then(entityUpdate.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		
		entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/Ga",
				HttpMethod.GET, requestDefault, typeRef);
		teams = entity.getBody();
		team = teams.get(0);
		then(team.getName()).isEqualTo("Gama2");
	}

	@Test
	public void deleteTeam() throws Exception {
		ParameterizedTypeReference<List<Team>> typeRef = new ParameterizedTypeReference<List<Team>>() {
		};
		
		ResponseEntity<List<Team>> entity = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/Ga",
				HttpMethod.GET, requestDefault, typeRef);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<Team> teams = entity.getBody();
		Team team = teams.get(0);
		
		ResponseEntity<Team> entityDelete = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/"+team.getId(),
				HttpMethod.DELETE, requestDefault, Team.class);
		
		then(entityDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	public void addTeamMember() throws Exception {
		Team team = new Team("team");

		RestTemplate restTemplate = restTemplate();
		String urlTeam = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestTeam = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entityTeam = restTemplate.postForEntity(urlTeam, requestTeam, Team.class);
		
		then(entityTeam.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		String urlMember = "http://localhost:" + this.port + "/members/";

		Member member = new Member("member");
		
		final HttpEntity<Member> requestMember = new HttpEntity<Member>(member, headers);
		final ResponseEntity<Team> entityMember = restTemplate.postForEntity(urlMember, requestMember, Team.class);
		
		then(entityMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ParameterizedTypeReference<List<Team>> typeRefTeam = new ParameterizedTypeReference<List<Team>>() {
		};
		ParameterizedTypeReference<List<Member>> typeRefMember = new ParameterizedTypeReference<List<Member>>() {
		};
		
		ResponseEntity<List<Team>> entityFindTeam = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/team",
				HttpMethod.GET, requestDefault, typeRefTeam);
		
		Team teamInserted = entityFindTeam.getBody().get(0);
		
		ResponseEntity<List<Member>> entityFindMember = this.testRestTemplate.exchange("http://localhost:" + this.port + "/members/search/member",
				HttpMethod.GET, requestDefault, typeRefMember);
		
		Member memberInserted = entityFindMember.getBody().get(0);
		
		urlTeam = urlTeam+teamInserted.getId()+"/";
		
		final HttpEntity<Member> requestTeamMember = new HttpEntity<Member>(memberInserted, headers);
		final ResponseEntity<Team> entityTeamMember = restTemplate.postForEntity(urlTeam, requestTeamMember, Team.class);
		
		then(entityTeamMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	public void addRemoveMemberFromTeam() throws Exception {
		Team team = new Team("team99");

		RestTemplate restTemplate = restTemplate();
		String urlTeam = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestTeam = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entityTeam = restTemplate.postForEntity(urlTeam, requestTeam, Team.class);
		
		then(entityTeam.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		String urlMember = "http://localhost:" + this.port + "/members/";

		Member member = new Member("member99");
		
		final HttpEntity<Member> requestMember = new HttpEntity<Member>(member, headers);
		final ResponseEntity<Team> entityMember = restTemplate.postForEntity(urlMember, requestMember, Team.class);
		
		then(entityMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ParameterizedTypeReference<List<Team>> typeRefTeam = new ParameterizedTypeReference<List<Team>>() {
		};
		ParameterizedTypeReference<List<Member>> typeRefMember = new ParameterizedTypeReference<List<Member>>() {
		};
		
		ResponseEntity<List<Team>> entityFindTeam = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/team",
				HttpMethod.GET, requestDefault, typeRefTeam);
		
		Team teamInserted = entityFindTeam.getBody().get(0);
		
		ResponseEntity<List<Member>> entityFindMember = this.testRestTemplate.exchange("http://localhost:" + this.port + "/members/search/member",
				HttpMethod.GET, requestDefault, typeRefMember);
		
		Member memberInserted = entityFindMember.getBody().get(0);
		
		urlTeam = urlTeam+teamInserted.getId()+"/";
		
		final HttpEntity<Member> requestTeamMember = new HttpEntity<Member>(memberInserted, headers);
		final ResponseEntity<Team> entityTeamMember = restTemplate.postForEntity(urlTeam, requestTeamMember, Team.class);
		
		then(entityTeamMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	public void shouldNotAddSameTeamMember() throws Exception {
		Team team = new Team("team2");

		RestTemplate restTemplate = restTemplate();
		String urlTeam = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestTeam = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entityTeam = restTemplate.postForEntity(urlTeam, requestTeam, Team.class);
		
		then(entityTeam.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		String urlMember = "http://localhost:" + this.port + "/members/";

		Member member = new Member("member2");
		
		final HttpEntity<Member> requestMember = new HttpEntity<Member>(member, headers);
		final ResponseEntity<Team> entityMember = restTemplate.postForEntity(urlMember, requestMember, Team.class);
		
		then(entityMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ParameterizedTypeReference<List<Team>> typeRefTeam = new ParameterizedTypeReference<List<Team>>() {};
		ParameterizedTypeReference<List<Member>> typeRefMember = new ParameterizedTypeReference<List<Member>>() {};
		
		ResponseEntity<List<Team>> entityFindTeam = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/team2",
				HttpMethod.GET, requestDefault, typeRefTeam);
		
		Team teamInserted = entityFindTeam.getBody().get(0);
		
		ResponseEntity<List<Member>> entityFindMember = this.testRestTemplate.exchange("http://localhost:" + this.port + "/members/search/member2",
				HttpMethod.GET, requestDefault, typeRefMember);
		
		Member memberInserted = entityFindMember.getBody().get(0);
		
		urlTeam = urlTeam+teamInserted.getId()+"/";
		
		final HttpEntity<Member> requestTeamMember = new HttpEntity<Member>(memberInserted, headers);
		ResponseEntity<Void> entityTeamMember = restTemplate.postForEntity(urlTeam, requestTeamMember, Void.class);
		
		then(entityTeamMember.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		try {
			restTemplate.postForEntity(urlTeam, requestTeamMember, Void.class);
		} catch (HttpClientErrorException e) {
			then(HttpStatus.valueOf(e.getRawStatusCode())).isEqualTo(HttpStatus.CONFLICT);
		}
	}
	
	@Test
	public void shouldNotAddTeamMemberInexistent() throws Exception {
		Team team = new Team("team3");

		RestTemplate restTemplate = restTemplate();
		String urlTeam = "http://localhost:" + this.port + "/teams/";

		final HttpEntity<Team> requestTeam = new HttpEntity<Team>(team, headers);
		final ResponseEntity<Team> entityTeam = restTemplate.postForEntity(urlTeam, requestTeam, Team.class);
		
		then(entityTeam.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ParameterizedTypeReference<List<Team>> typeRefTeam = new ParameterizedTypeReference<List<Team>>() {};
		
		ResponseEntity<List<Team>> entityFindTeam = this.testRestTemplate.exchange("http://localhost:" + this.port + "/teams/search/team2",
				HttpMethod.GET, requestDefault, typeRefTeam);
		
		Team teamInserted = entityFindTeam.getBody().get(0);
		
		Member memberInserted = new Member("new");
		memberInserted.setId("oooooo0000000");
		
		urlTeam = urlTeam+teamInserted.getId()+"/";
		
		final HttpEntity<Member> requestTeamMember = new HttpEntity<Member>(memberInserted, headers);
		
		try {
			restTemplate.postForEntity(urlTeam, requestTeamMember, Void.class);
		} catch (HttpClientErrorException e) {
			then(HttpStatus.valueOf(e.getRawStatusCode())).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}

	private RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		return new RestTemplate(Arrays.asList(converter));
	}

}