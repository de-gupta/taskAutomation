package de.gupta.automation.task.framework.registry.internal;

import de.gupta.automation.task.framework.registry.TaskCliDescriptor;
import de.gupta.automation.task.framework.registry.TaskCliOutputRenderer;
import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;

import java.util.List;

public record DefaultTaskCliDescriptor<O>(String commandName,
                                          String description,
                                          List<TaskOptionDescriptor> options,
                                          TaskCliOutputRenderer<O> outputRenderer) implements TaskCliDescriptor<O>
{
}