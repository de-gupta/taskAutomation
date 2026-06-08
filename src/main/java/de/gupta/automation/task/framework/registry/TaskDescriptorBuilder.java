package de.gupta.automation.task.framework.registry;

import de.gupta.automation.task.framework.domain.port.*;
import de.gupta.automation.task.framework.registry.internal.DefaultTaskCliDescriptor;
import de.gupta.automation.task.framework.registry.internal.DefaultTaskDescriptor;
import de.gupta.automation.task.framework.registry.internal.DefaultTaskOptionDescriptor;
import de.gupta.automation.task.framework.registry.internal.DefaultTaskRestDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface TaskDescriptorBuilder<O, MO, OO>
{
	static TaskDescriptorBuilderNameStep task(final String name)
	{
		return new Builder<>(name);
	}

	TaskDescriptor<O, MO, OO> build();

	interface TaskDescriptorBuilderNameStep
	{
		TaskDescriptorBuilderVersionStep version(String version);
	}

	interface TaskDescriptorBuilderVersionStep
	{
		<I, O, MO, OO> TaskDescriptorBuilderTypedStep<I, O, MO, OO> types(Class<I> inputType,
		                                                                  Class<O> outputType,
		                                                                  Class<MO> mandatoryOptionsType,
		                                                                  Class<OO> optionalOptionsType);
	}

	interface TaskDescriptorBuilderTypedStep<I, O, MO, OO>
	{
		TaskDescriptorBuilderTypedStep<I, O, MO, OO> function(TaskFunction<I, O> function);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> assembler(TaskInputAssembler<I, MO, OO> assembler);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> validator(TaskValidator<MO, OO> validator);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> mandatoryOption(String propertyKey, Class<?> valueType,
		                                                             Consumer<TaskOptionBuilder> consumer);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> optionalOption(String propertyKey, Class<?> valueType,
		                                                            Consumer<TaskOptionBuilder> consumer);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> cli(Consumer<TaskCliBuilder<O>> consumer);

		TaskDescriptorBuilderTypedStep<I, O, MO, OO> rest(Consumer<TaskRestBuilder<O>> consumer);

		TaskDescriptor<O, MO, OO> build();
	}

	interface TaskOptionBuilder
	{
		TaskOptionBuilder cliName(String cliName);

		TaskOptionBuilder restName(String restName);

		TaskOptionBuilder description(String description);

		TaskOptionBuilder documentedDefaultValue(String documentedDefaultValue);
	}

	interface TaskCliBuilder<O>
	{
		TaskCliBuilder<O> commandName(String commandName);

		TaskCliBuilder<O> description(String description);

		TaskCliBuilder<O> outputRenderer(TaskCliOutputRenderer<O> outputRenderer);
	}

	interface TaskRestBuilder<O>
	{
		TaskRestBuilder<O> path(String path);

		TaskRestBuilder<O> description(String description);

		TaskRestBuilder<O> produces(String produces);

		TaskRestBuilder<O> outputRenderer(TaskRestOutputRenderer<O> outputRenderer);
	}

	final class Builder<I, O, MO, OO> implements TaskDescriptorBuilder<O, MO, OO>,
			TaskDescriptorBuilderNameStep,
			TaskDescriptorBuilderVersionStep,
			TaskDescriptorBuilderTypedStep<I, O, MO, OO>
	{
		private final String name;
		private final List<TaskOptionDescriptor> options = new ArrayList<>();
		private String version;
		private Class<I> inputType;
		private Class<O> outputType;
		private Class<MO> mandatoryOptionsType;
		private Class<OO> optionalOptionsType;
		private TaskFunction<I, O> function;
		private TaskInputAssembler<I, MO, OO> assembler;
		private TaskValidator<MO, OO> validator;
		private String currentPropertyKey;
		private Class<?> currentValueType;
		private boolean currentMandatory;
		private String currentCliName;
		private String currentRestName;
		private String currentDescription = "";
		private String currentDocumentedDefaultValue;
		private String cliCommandName;
		private String cliDescription = "";
		private TaskCliOutputRenderer<O> cliOutputRenderer = String::valueOf;
		private String restPath;
		private String restDescription = "";
		private String restProduces = "application/json";
		private TaskRestOutputRenderer<O> restOutputRenderer = output -> output;

		@Override
		public TaskDescriptorBuilderVersionStep version(final String version)
		{
			this.version = version;
			return this;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <NI, NO, NMO, NOO> TaskDescriptorBuilderTypedStep<NI, NO, NMO, NOO> types(final Class<NI> inputType,
		                                                                                 final Class<NO> outputType,
		                                                                                 final Class<NMO> mandatoryOptionsType,
		                                                                                 final Class<NOO> optionalOptionsType)
		{
			this.inputType = (Class<I>) inputType;
			this.outputType = (Class<O>) outputType;
			this.mandatoryOptionsType = (Class<MO>) mandatoryOptionsType;
			this.optionalOptionsType = (Class<OO>) optionalOptionsType;
			return (TaskDescriptorBuilderTypedStep<NI, NO, NMO, NOO>) this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> function(final TaskFunction<I, O> function)
		{
			this.function = function;
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> assembler(final TaskInputAssembler<I, MO, OO> assembler)
		{
			this.assembler = assembler;
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> validator(final TaskValidator<MO, OO> validator)
		{
			this.validator = validator;
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> mandatoryOption(final String propertyKey,
		                                                                    final Class<?> valueType,
		                                                                    final Consumer<TaskOptionBuilder> consumer)
		{
			startOption(propertyKey, valueType, true);
			consumer.accept(new OptionBuilderImpl());
			finishOption();
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> optionalOption(final String propertyKey,
		                                                                   final Class<?> valueType,
		                                                                   final Consumer<TaskOptionBuilder> consumer)
		{
			startOption(propertyKey, valueType, false);
			consumer.accept(new OptionBuilderImpl());
			finishOption();
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> cli(final Consumer<TaskCliBuilder<O>> consumer)
		{
			consumer.accept(new CliBuilderImpl());
			return this;
		}

		@Override
		public TaskDescriptorBuilderTypedStep<I, O, MO, OO> rest(final Consumer<TaskRestBuilder<O>> consumer)
		{
			consumer.accept(new RestBuilderImpl());
			return this;
		}

		@Override
		public TaskDescriptor<O, MO, OO> build()
		{
			final TaskFacade<O, MO, OO> facade = TaskFacade.create(assembler, function, validator);
			return new DefaultTaskDescriptor<>(
					name,
					version,
					inputType,
					outputType,
					mandatoryOptionsType,
					optionalOptionsType,
					facade,
					new DefaultTaskCliDescriptor<>(cliCommandName, cliDescription, List.copyOf(options),
							cliOutputRenderer),
					new DefaultTaskRestDescriptor<>(restPath, restDescription, restProduces, List.copyOf(options),
							restOutputRenderer));
		}

		private void startOption(final String propertyKey, final Class<?> valueType, final boolean mandatory)
		{
			currentPropertyKey = propertyKey;
			currentValueType = valueType;
			currentMandatory = mandatory;
			currentCliName = "--" + propertyKey;
			currentRestName = propertyKey;
			currentDescription = "";
			currentDocumentedDefaultValue = null;
		}

		private void finishOption()
		{
			options.add(new DefaultTaskOptionDescriptor(
					currentPropertyKey,
					currentValueType,
					currentMandatory,
					currentCliName,
					currentRestName,
					currentDescription,
					currentDocumentedDefaultValue));
		}

		Builder(final String name)
		{
			this.name = name;
		}

		private final class OptionBuilderImpl implements TaskOptionBuilder
		{
			@Override
			public TaskOptionBuilder cliName(final String cliName)
			{
				currentCliName = cliName;
				return this;
			}

			@Override
			public TaskOptionBuilder restName(final String restName)
			{
				currentRestName = restName;
				return this;
			}

			@Override
			public TaskOptionBuilder description(final String description)
			{
				currentDescription = description;
				return this;
			}

			@Override
			public TaskOptionBuilder documentedDefaultValue(final String documentedDefaultValue)
			{
				currentDocumentedDefaultValue = documentedDefaultValue;
				return this;
			}
		}

		private final class CliBuilderImpl implements TaskCliBuilder<O>
		{
			@Override
			public TaskCliBuilder<O> commandName(final String commandName)
			{
				cliCommandName = commandName;
				return this;
			}

			@Override
			public TaskCliBuilder<O> description(final String description)
			{
				cliDescription = description;
				return this;
			}

			@Override
			public TaskCliBuilder<O> outputRenderer(final TaskCliOutputRenderer<O> outputRenderer)
			{
				cliOutputRenderer = outputRenderer;
				return this;
			}
		}

		private final class RestBuilderImpl implements TaskRestBuilder<O>
		{
			@Override
			public TaskRestBuilder<O> path(final String path)
			{
				restPath = path;
				return this;
			}

			@Override
			public TaskRestBuilder<O> description(final String description)
			{
				restDescription = description;
				return this;
			}

			@Override
			public TaskRestBuilder<O> produces(final String produces)
			{
				restProduces = produces;
				return this;
			}

			@Override
			public TaskRestBuilder<O> outputRenderer(final TaskRestOutputRenderer<O> outputRenderer)
			{
				restOutputRenderer = outputRenderer;
				return this;
			}
		}
	}
}