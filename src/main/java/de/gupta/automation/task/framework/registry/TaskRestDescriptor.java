package de.gupta.automation.task.framework.registry;

import java.util.List;

public interface TaskRestDescriptor<O>
{
	String path();

	String description();

	String produces();

	List<TaskOptionDescriptor> options();

	TaskRestOutputRenderer<O> outputRenderer();
}
