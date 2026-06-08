package de.gupta.automation.task.framework;

import de.gupta.automation.task.framework.rest.FrameworkRestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FrameworkRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FrameworkRestApplicationTest
{
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@TempDir
	Path tempDir;

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

	@Test
	void shouldExposeDescriptorDrivenOpenApiDocumentation()
	{
		final ResponseEntity<String> response = testRestTemplate.getForEntity(
				"http://localhost:" + port + "/v3/api-docs",
				String.class);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody())
				.contains("\"/api/tasks/print-text/execute\"")
				.contains("\"/api/tasks/create-dat-file/execute\"")
				.doesNotContain("\"/api/tasks/{taskName}/execute\"")
				.contains("\"PrintTextRequest\"")
				.contains("\"CreateDatFileRequest\"")
				.contains("\"text\"")
				.contains("\"repeatCount\"")
				.contains("\"prefix\"")
				.contains("\"upperCase\"")
				.contains("\"fileName\"")
				.contains("\"overwrite\"")
				.contains("\"default\":\"OUTPUT: \"");
	}

	@Test
	void shouldExecuteDescriptorDrivenCreateDatFileEndpoint() throws Exception
	{
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final Path filePath = tempDir.resolve("rest-created-file");
		final HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
				"fileName", filePath.toString(),
				"text", "rest text",
				"upperCase", true,
				"overwrite", false), headers);

		final ResponseEntity<String> response = testRestTemplate.postForEntity(
				"http://localhost:" + port + "/api/tasks/create-dat-file/execute",
				request,
				String.class);

		final Path expectedPath = Path.of(filePath.toAbsolutePath() + ".dat");
		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isEqualTo(expectedPath.toString());
		assertThat(Files.readString(expectedPath)).isEqualTo("REST TEXT");
	}
}