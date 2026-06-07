package de.gupta.automation.task.api.application;

import de.gupta.automation.task.adapter.TaskAPIFacadeAdapter;
import de.gupta.automation.task.facade.TaskServiceFacade;

final class TaskAPIImpl<API, I, O, MO, OO> implements TaskAPI<API, O>
{
	private final TaskServiceFacade<I, O, MO, OO> serviceFacade;
	private final TaskAPIFacadeAdapter<API, I, O, MO, OO> adapter;

	@Override
	public O execute(final API input)
	{
		return serviceFacade.execute(adapter.adapt(input).input(),
				adapter.adapt(input).mandatoryOptions(),
				adapter.adapt(input).optionalOptions());
	}

	TaskAPIImpl(final TaskServiceFacade<I, O, MO, OO> serviceFacade,
	            final TaskAPIFacadeAdapter<API, I, O, MO, OO> adapter)
	{
		this.serviceFacade = serviceFacade;
		this.adapter = adapter;
	}
}