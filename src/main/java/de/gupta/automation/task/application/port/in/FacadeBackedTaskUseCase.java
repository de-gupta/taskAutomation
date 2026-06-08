package de.gupta.automation.task.application.port.in;

import de.gupta.automation.task.domain.port.TaskFacade;

final class FacadeBackedTaskUseCase<O, MO, OO> implements TaskUseCase<O, MO, OO>
{
	private final TaskFacade<O, MO, OO> facade;

	FacadeBackedTaskUseCase(final TaskFacade<O, MO, OO> facade)
	{
		this.facade = facade;
	}

	@Override
	public O execute(final MO mandatoryOptions, final OO optionalOptions)
	{
		return facade.execute(mandatoryOptions, optionalOptions);
	}
}
