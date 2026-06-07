package de.gupta.automation.task.service;

public interface TaskService<I, O, MO, OO>
{
	O execute(I input, MO mandatoryOptions, OO optionalOptions);
}