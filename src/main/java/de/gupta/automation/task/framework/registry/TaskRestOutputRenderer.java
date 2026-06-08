package de.gupta.automation.task.framework.registry;

public interface TaskRestOutputRenderer<O>
{
	Object render(O output);
}
