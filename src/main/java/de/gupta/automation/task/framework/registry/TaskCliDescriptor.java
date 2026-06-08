package de.gupta.automation.task.framework.registry;

import java.util.List;

public interface TaskCliDescriptor<O>
{
	String commandName();

	String description();

	List<TaskOptionDescriptor> options();

	TaskCliOutputRenderer<O> outputRenderer();
}
