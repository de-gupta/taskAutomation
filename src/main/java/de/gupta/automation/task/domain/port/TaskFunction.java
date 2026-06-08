package de.gupta.automation.task.domain.port;

public interface TaskFunction<I, O>
{
	O execute(I input);
}
