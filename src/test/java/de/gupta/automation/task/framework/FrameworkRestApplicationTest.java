package de.gupta.automation.task.framework;

import de.gupta.automation.task.framework.rest.FrameworkRestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FrameworkRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FrameworkRestApplicationTest
{
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void shouldExecuteDescriptorDrivenRestEndpoint()
	{
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
				"text", "sample",
				"repeatCount", 2,
				"prefix", "REST:",
				"upperCase", false), headers);

		final ResponseEntity<String> response = testRestTemplate.postForEntity(
				"http://localhost:" + port + "/api/tasks/print-text/execute",
				request,
				String.class);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo("REST:sample" + System.lineSeparator() + "REST:sample");
	}
}