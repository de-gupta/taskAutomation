package de.gupta.automation.task.framework.registry;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public final class SpringTaskRegistry implements TaskRegistry
{
	private final List<TaskDescriptor<?, ?, ?>> descriptors;

	public SpringTaskRegistry(final List<TaskDescriptor<?, ?, ?>> descriptors)
	{
		assertUnique(descriptors.stream().map(TaskDescriptor::name).toList(), "task name");
		assertUnique(descriptors.stream().map(descriptor -> descriptor.cli().commandName()).toList(),
				"CLI command name");
		assertUnique(descriptors.stream().map(descriptor -> descriptor.rest().path()).toList(), "REST path");
		assertSupportedRestPaths(descriptors);
		this.descriptors = List.copyOf(descriptors);
	}

	@Override
	public Collection<TaskDescriptor<?, ?, ?>> descriptors()
	{
		return descriptors;
	}

	@Override
	public Optional<TaskDescriptor<?, ?, ?>> findByName(final String taskName)
	{
		return descriptors.stream()
		                  .filter(descriptor -> descriptor.name().equals(taskName))
		                  .findFirst();
	}

	private static void assertUnique(final List<String> values, final String label)
	{
		final Set<String> seen = new HashSet<>();
		final Set<String> duplicates = new HashSet<>();
		for (final String value : values)
		{
			if (!seen.add(value))
			{
				duplicates.add(value);
			}
		}
		if (!duplicates.isEmpty())
		{
			throw new IllegalStateException("Duplicate " + label + "s are not allowed: " + duplicates);
		}
	}

	private static void assertSupportedRestPaths(final List<TaskDescriptor<?, ?, ?>> descriptors)
	{
		for (final TaskDescriptor<?, ?, ?> descriptor : descriptors)
		{
			final String expectedPath = "/api/tasks/" + descriptor.name() + "/execute";
			if (!expectedPath.equals(descriptor.rest().path()))
			{
				throw new IllegalStateException(
						"REST path must currently match the generic runtime pattern. Expected "
								+ expectedPath
								+ " but got "
								+ descriptor.rest().path()
								+ " for task "
								+ descriptor.name());
			}
		}
	}
}