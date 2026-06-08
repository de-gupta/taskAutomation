package de.gupta.automation.task.framework.registry;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;

import java.util.Collection;
import java.util.Optional;

public interface TaskRegistry
{
	Collection<TaskDescriptor<?, ?, ?>> descriptors();

	Optional<TaskDescriptor<?, ?, ?>> findByName(String taskName);
}