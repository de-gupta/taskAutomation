package de.gupta.automation.task.framework.registry.internal;

import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;
import de.gupta.automation.task.framework.registry.TaskRestDescriptor;
import de.gupta.automation.task.framework.registry.TaskRestOutputRenderer;

import java.util.List;

public record DefaultTaskRestDescriptor<O>(String path,
                                           String description,
                                           String produces,
                                           List<TaskOptionDescriptor> options,
                                           TaskRestOutputRenderer<O> outputRenderer) implements TaskRestDescriptor<O>
{
}