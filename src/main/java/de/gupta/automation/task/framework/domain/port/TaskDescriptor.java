package de.gupta.automation.task.framework.domain.port;

import de.gupta.automation.task.framework.registry.TaskCliDescriptor;
import de.gupta.automation.task.framework.registry.TaskRestDescriptor;

public interface TaskDescriptor<O, MO, OO>
{
	String name();

	String version();

	Class<?> inputType();

	Class<O> outputType();

	Class<MO> mandatoryOptionsType();

	Class<OO> optionalOptionsType();

	TaskFacade<O, MO, OO> facade();

	TaskCliDescriptor<O> cli();

	TaskRestDescriptor<O> rest();
}
