package de.gupta.automation.task.framework.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TaskOptionsBinder
{
	private final ObjectMapper objectMapper;

	public TaskOptionsBinder(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	public <T> T bind(final Class<T> targetType,
	                  final List<TaskOptionDescriptor> optionDescriptors,
	                  final Map<String, ?> values,
	                  final boolean mandatory)
	{
		final Map<String, Object> filteredValues = new LinkedHashMap<>();
		for (final TaskOptionDescriptor descriptor : optionDescriptors)
		{
			if (descriptor.mandatory() != mandatory)
			{
				continue;
			}
			final Object rawValue = values.get(descriptor.restName());
			if (rawValue == null)
			{
				filteredValues.put(descriptor.propertyKey(), defaultValue(descriptor.valueType()));
				continue;
			}
			filteredValues.put(descriptor.propertyKey(), rawValue);
		}
		return objectMapper.convertValue(filteredValues, targetType);
	}

	public Map<String, Object> cliValues(final List<TaskOptionDescriptor> optionDescriptors,
	                                     final Map<String, Object> parsedValues)
	{
		final Map<String, Object> values = new LinkedHashMap<>();
		for (final TaskOptionDescriptor descriptor : optionDescriptors)
		{
			final Object rawValue = parsedValues.get(descriptor.cliName());
			values.put(descriptor.restName(), rawValue == null ? defaultValue(descriptor.valueType()) : rawValue);
		}
		return values;
	}

	private static Object defaultValue(final Class<?> valueType)
	{
		if (!valueType.isPrimitive())
		{
			return null;
		}
		if (valueType == boolean.class)
		{
			return false;
		}
		if (valueType == int.class)
		{
			return 0;
		}
		if (valueType == long.class)
		{
			return 0L;
		}
		if (valueType == double.class)
		{
			return 0d;
		}
		if (valueType == float.class)
		{
			return 0f;
		}
		if (valueType == short.class)
		{
			return (short) 0;
		}
		if (valueType == byte.class)
		{
			return (byte) 0;
		}
		if (valueType == char.class)
		{
			return '\0';
		}
		throw new IllegalArgumentException("Unsupported primitive type: " + valueType.getName());
	}
}