package de.gupta.automation.task.template.domain.port;

public interface TaskInputAssembler<I, MO, OO>
{
	I assemble(MO mandatoryOptions, OO optionalOptions);
}