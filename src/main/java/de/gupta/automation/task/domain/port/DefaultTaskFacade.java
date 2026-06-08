package de.gupta.automation.task.domain.port;

final class DefaultTaskFacade<I, O, MO, OO> implements TaskFacade<O, MO, OO>
{
	private final TaskInputAssembler<I, MO, OO> assembler;
	private final TaskFunction<I, O> function;
	private final TaskValidator<MO, OO> validator;

	@Override
	public O execute(final MO mandatoryOptions, final OO optionalOptions)
	{
		validator.validateMandatory(mandatoryOptions);
		validator.validateOptional(optionalOptions);
		final I input = assembler.assemble(mandatoryOptions, optionalOptions);
		return function.execute(input);
	}

	DefaultTaskFacade(final TaskInputAssembler<I, MO, OO> assembler, final TaskFunction<I, O> function,
	                  final TaskValidator<MO, OO> validator)
	{
		this.assembler = assembler;
		this.function = function;
		this.validator = validator;
	}
}
