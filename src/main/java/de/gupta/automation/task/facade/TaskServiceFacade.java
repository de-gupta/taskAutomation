package de.gupta.automation.task.facade;

public interface TaskServiceFacade<I, O, MO, OO>
{
	O execute(I input, MO mandatoryOptions, OO optionalOptions);
}