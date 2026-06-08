package de.gupta.automation.task.domain.port;

public interface TaskInputAssembler<I, MO, OO>
{
	I assemble(MO mandatoryOptions, OO optionalOptions);
}
