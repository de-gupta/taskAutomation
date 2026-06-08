package de.gupta.automation.task.framework.domain.port;

import de.gupta.automation.task.framework.domain.internal.DefaultTaskFacade;

public interface TaskFacade<O, MO, OO>
{
	static <I, O, MO, OO> TaskFacade<O, MO, OO> create(final TaskInputAssembler<I, MO, OO> assembler,
	                                                   final TaskFunction<I, O> function,
	                                                   final TaskValidator<MO, OO> validator)
	{
		return new DefaultTaskFacade<>(assembler, function, validator);
	}

	O execute(MO mandatoryOptions, OO optionalOptions);
}