package de.gupta.automation.task.framework.registry;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public final class SpringTaskRegistry implements TaskRegistry
{
	private final List<TaskDescriptor<?, ?, ?>> descriptors;

	public SpringTaskRegistry(final List<TaskDescriptor<?, ?, ?>> descriptors)
	{
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
}