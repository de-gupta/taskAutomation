package de.gupta.automation.task.framework.domain.port;

public interface TaskInputAssembler<I, MO, OO>
{
	I assemble(MO mandatoryOptions, OO optionalOptions);
}
