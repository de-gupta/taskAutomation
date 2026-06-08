package de.gupta.automation.task.implementations.text.print.adapter.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.gupta.automation.task.implementations.text.print.api.PrintTextApplicationService;

@RestController
@RequestMapping("/api/tasks/print-text")
final class PrintTextController
{
	private final PrintTextApplicationService applicationService;

	PrintTextController(final PrintTextApplicationService applicationService)
	{
		this.applicationService = applicationService;
	}

	@PostMapping(path = "/execute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	String execute(@RequestBody final PrintTextRequest request)
	{
		return applicationService.execute(request.text(), request.repeatCount(), request.prefix(), request.upperCase());
	}
}