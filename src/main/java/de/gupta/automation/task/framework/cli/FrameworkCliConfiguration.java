package de.gupta.automation.task.framework.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;
import de.gupta.automation.task.framework.registry.TaskRegistry;
import de.gupta.automation.task.framework.runtime.TaskOptionsBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

@Configuration(proxyBeanMethods = false)
public class FrameworkCliConfiguration
{
	@Bean
	CommandLine frameworkCommandLine(final TaskRegistry taskRegistry, final ObjectMapper objectMapper)
	{
		final CommandSpec rootSpec = CommandSpec.create().name("tasks");
		rootSpec.addOption(helpOption());
		final CommandLine rootCommand = new CommandLine(rootSpec);
		final TaskOptionsBinder optionsBinder = new TaskOptionsBinder(objectMapper);

		for (final TaskDescriptor<?, ?, ?> descriptor : taskRegistry.descriptors())
		{
			rootCommand.addSubcommand(descriptor.cli().commandName(), createSubcommand(descriptor, optionsBinder));
		}
		return rootCommand;
	}

	private static CommandLine createSubcommand(final TaskDescriptor<?, ?, ?> descriptor,
	                                            final TaskOptionsBinder optionsBinder)
	{
		final CommandSpec spec = CommandSpec.create()
		                                    .name(descriptor.cli().commandName());
		spec.addOption(helpOption());
		spec.usageMessage().description(descriptor.cli().description());

		for (final TaskOptionDescriptor option : descriptor.cli().options())
		{
			final OptionSpec.Builder builder = OptionSpec.builder(option.cliName())
			                                             .required(option.mandatory())
			                                             .type(option.valueType())
			                                             .description(option.description());
			if (option.valueType() == boolean.class || option.valueType() == Boolean.class)
			{
				builder.defaultValue("false");
			}
			spec.addOption(builder.build());
		}

		return new CommandLine(spec);
	}

	private static OptionSpec helpOption()
	{
		return OptionSpec.builder("-h", "--help")
		                 .usageHelp(true)
		                 .description("Show this help message and exit.")
		                 .build();
	}
}
