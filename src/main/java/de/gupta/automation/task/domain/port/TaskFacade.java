package de.gupta.automation.task.domain.port;

public interface TaskFacade<O, MO, OO>
{
	O execute(MO mandatoryOptions, OO optionalOptions);

	static <I, O, MO, OO> TaskFacade<O, MO, OO> create(final TaskInputAssembler<I, MO, OO> assembler,
	                                                   final TaskFunction<I, O> function,
	                                                   final TaskValidator<MO, OO> validator)
	{
		return new DefaultTaskFacade<>(assembler, function, validator);
	}
}
