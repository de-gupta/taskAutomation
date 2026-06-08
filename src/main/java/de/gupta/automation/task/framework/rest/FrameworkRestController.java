package de.gupta.automation.task.framework.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskRegistry;
import de.gupta.automation.task.framework.runtime.TaskOptionsBinder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public final class FrameworkRestController
{
	private final TaskRegistry taskRegistry;
	private final TaskOptionsBinder optionsBinder;

	public FrameworkRestController(final TaskRegistry taskRegistry, final ObjectMapper objectMapper)
	{
		this.taskRegistry = taskRegistry;
		this.optionsBinder = new TaskOptionsBinder(objectMapper);
	}

	@PostMapping("/api/tasks/{taskName}/execute")
	ResponseEntity<?> execute(@PathVariable("taskName") final String taskName,
	                          @RequestBody final Map<String, Object> body)
	{
		final TaskDescriptor<?, ?, ?> descriptor = taskRegistry.findByName(taskName)
		                                                       .orElseThrow(() -> new IllegalArgumentException(
				                                                       "Unknown task: " + taskName));

		final Object mandatoryOptions = optionsBinder.bind(
				descriptor.mandatoryOptionsType(),
				descriptor.rest().options(),
				body,
				true);
		final Object optionalOptions = optionsBinder.bind(
				descriptor.optionalOptionsType(),
				descriptor.rest().options(),
				body,
				false);
		final Object output = execute(descriptor, mandatoryOptions, optionalOptions);
		return ResponseEntity.ok()
		                     .header(HttpHeaders.CONTENT_TYPE, descriptor.rest().produces())
		                     .body(renderRest(descriptor, output));
	}

	@SuppressWarnings("unchecked")
	private static <O, MO, OO> Object execute(final TaskDescriptor<?, ?, ?> descriptor,
	                                          final Object mandatoryOptions,
	                                          final Object optionalOptions)
	{
		return ((TaskDescriptor<O, MO, OO>) descriptor).facade().execute((MO) mandatoryOptions, (OO) optionalOptions);
	}

	@SuppressWarnings("unchecked")
	private static <O> Object renderRest(final TaskDescriptor<?, ?, ?> descriptor, final Object output)
	{
		return ((TaskDescriptor<O, ?, ?>) descriptor).rest().outputRenderer().render((O) output);
	}
}