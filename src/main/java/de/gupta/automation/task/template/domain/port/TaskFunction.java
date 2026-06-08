package de.gupta.automation.task.template.domain.port;

public interface TaskFunction<I, O>
{
	O execute(I input);
}