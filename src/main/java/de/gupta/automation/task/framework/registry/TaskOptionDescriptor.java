package de.gupta.automation.task.framework.registry;

public interface TaskOptionDescriptor
{
	String propertyKey();

	Class<?> valueType();

	boolean mandatory();

	String cliName();

	String restName();

	String description();

	String documentedDefaultValue();
}
