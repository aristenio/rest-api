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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private HttpEntity<String> request;

	@Before
	public void setUp() {

		String plainCreds = "user:pass";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		request = new HttpEntity<String>(headers);
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
				HttpMethod.GET, request, List.class);

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
				HttpMethod.GET, request, typeRef);

		then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<Team> teams = entity.getBody();

		then(teams.size()).isEqualTo(3);

		Team teamGama = teams.get(0);
		then(teamGama.getName()).isEqualTo("Gama");

		Team teamBeta = teams.get(1);
		then(teamBeta.getName()).isEqualTo("Beta");

		Team teamAlpha = teams.get(2);
		then(teamAlpha.getName()).isEqualTo("Alpha");
	}

	@Test
	public void shouldReturnThreeInitialTeamsOrdering() {
		RestTemplate restTemplate = restTemplate();
		String url = "http://localhost:" + this.port + "/teams?sort=name";

		ResponseEntity<PagedResources<Team>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request,
				new ParameterizedTypeReference<PagedResources<Team>>() {
				}, port, 0, 100);
		PagedResources<Team> resources = responseEntity.getBody();
		List<Team> teams = new ArrayList<>(resources.getContent());

		Team teamAlpha = teams.get(0);
		then(teamAlpha.getName()).isEqualTo("Alpha");

		Team teamBeta = teams.get(1);
		then(teamBeta.getName()).isEqualTo("Beta");

		Team teamGama = teams.get(2);
		then(teamGama.getName()).isEqualTo("Gama");

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