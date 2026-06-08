package de.gupta.automation.task.framework.registry.internal;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.domain.port.TaskFacade;
import de.gupta.automation.task.framework.registry.TaskCliDescriptor;
import de.gupta.automation.task.framework.registry.TaskRestDescriptor;

public record DefaultTaskDescriptor<O, MO, OO>(String name,
                                               String version,
                                               Class<?> inputType,
                                               Class<O> outputType,
                                               Class<MO> mandatoryOptionsType,
                                               Class<OO> optionalOptionsType,
                                               TaskFacade<O, MO, OO> facade,
                                               TaskCliDescriptor<O> cli,
                                               TaskRestDescriptor<O> rest) implements TaskDescriptor<O, MO, OO>
{
}
