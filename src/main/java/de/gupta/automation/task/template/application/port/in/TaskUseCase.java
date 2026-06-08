package de.gupta.automation.task.template.application.port.in;

import de.gupta.automation.task.template.domain.port.TaskFacade;

public interface TaskUseCase<O, MO, OO>
{
	O execute(MO mandatoryOptions, OO optionalOptions);

	static <O, MO, OO> TaskUseCase<O, MO, OO> create(final TaskFacade<O, MO, OO> facade)
	{
		return new FacadeBackedTaskUseCase<>(facade);
	}
}