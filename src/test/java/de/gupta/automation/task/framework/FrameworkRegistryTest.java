package de.gupta.automation.task.framework;

import de.gupta.automation.task.framework.cli.FrameworkCliApplication;
import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FrameworkCliApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FrameworkRegistryTest
{
	@Autowired
	private TaskRegistry taskRegistry;

	@Test
	void shouldRegisterDescriptorDrivenPrintTextTaskOnly()
	{
		assertThat(taskRegistry.findByName("print-text")).isPresent();
		assertThat(taskRegistry.descriptors())
				.extracting(TaskDescriptor::name)
				.containsExactly("print-text");
	}
}