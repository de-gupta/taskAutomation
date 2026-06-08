package de.gupta.automation.task.framework.registry;

public interface TaskCliOutputRenderer<O>
{
	String render(O output);
}
