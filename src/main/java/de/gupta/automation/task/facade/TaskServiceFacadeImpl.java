package de.gupta.automation.task.facade;

import de.gupta.automation.task.service.TaskService;

final class TaskServiceFacadeImpl<I, O, MO, OO> implements TaskServiceFacade<I, O, MO, OO>
{
	private final TaskService<I, O, MO, OO> service;

	@Override
	public O execute(final I input, final MO mandatoryOptions, final OO optionalOptions)
	{
		return service.execute(input, mandatoryOptions, optionalOptions);
	}

	TaskServiceFacadeImpl(final TaskService<I, O, MO, OO> service)
	{
		this.service = service;
	}
}