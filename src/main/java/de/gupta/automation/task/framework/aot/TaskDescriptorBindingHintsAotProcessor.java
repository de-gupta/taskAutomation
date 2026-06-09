package de.gupta.automation.task.framework.aot;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public final class TaskDescriptorBindingHintsAotProcessor implements BeanFactoryInitializationAotProcessor
{
	private static final BindingReflectionHintsRegistrar BINDING_HINTS = new BindingReflectionHintsRegistrar();

	@Override
	public BeanFactoryInitializationAotContribution processAheadOfTime(
			final ConfigurableListableBeanFactory beanFactory)
	{
		final String[] descriptorBeanNames = beanFactory.getBeanNamesForType(TaskDescriptor.class);
		if (descriptorBeanNames.length == 0)
		{
			return null;
		}

		final Set<Class<?>> bindingTypes = new LinkedHashSet<>();
		for (final String descriptorBeanName : descriptorBeanNames)
		{
			final TaskDescriptor<?, ?, ?> descriptor = beanFactory.getBean(descriptorBeanName, TaskDescriptor.class);
			bindingTypes.add(descriptor.inputType());
			bindingTypes.add(descriptor.outputType());
			bindingTypes.add(descriptor.mandatoryOptionsType());
			bindingTypes.add(descriptor.optionalOptionsType());
		}
		if (bindingTypes.isEmpty())
		{
			return null;
		}

		return (generationContext, beanFactoryInitializationCode) ->
				BINDING_HINTS.registerReflectionHints(
						generationContext.getRuntimeHints().reflection(),
						bindingTypes.toArray(Class[]::new));
	}
}
