package de.gupta.automation.task.framework.registry.internal;

import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;

public record DefaultTaskOptionDescriptor(String propertyKey,
                                          Class<?> valueType,
                                          boolean mandatory,
                                          String cliName,
                                          String restName,
                                          String description,
                                          String documentedDefaultValue) implements TaskOptionDescriptor
{
}
