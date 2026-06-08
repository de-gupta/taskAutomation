package de.gupta.automation.task.framework.domain.port;

public interface TaskFunction<I, O>
{
	O execute(I input);
}
