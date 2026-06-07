package de.gupta.automation.task.adapter;

import de.gupta.automation.task.facade.TaskServiceFacade;

public interface TaskAPIFacadeAdapter<API, I, O, MO, OO>
{
	FacadeInputShape<I, O, MO, OO> adapt(API api);
}