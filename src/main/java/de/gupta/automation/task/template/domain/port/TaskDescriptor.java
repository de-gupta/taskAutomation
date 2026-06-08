package de.gupta.automation.task.template.domain.port;

public interface TaskDescriptor<I, O, MO, OO>
{
	String name();

	String version();

	Class<I> inputType();

	Class<O> outputType();

	Class<MO> mandatoryOptionsType();

	Class<OO> optionalOptionsType();
}