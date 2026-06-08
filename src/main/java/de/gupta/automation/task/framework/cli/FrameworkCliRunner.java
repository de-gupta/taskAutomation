package de.gupta.automation.task.framework.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskOptionDescriptor;
import de.gupta.automation.task.framework.registry.TaskRegistry;
import de.gupta.automation.task.framework.runtime.TaskOptionsBinder;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public final class FrameworkCliRunner
{
	private final CommandLine commandLine;
	private final TaskRegistry taskRegistry;
	private final TaskOptionsBinder optionsBinder;

	public FrameworkCliRunner(final CommandLine commandLine, final TaskRegistry taskRegistry,
	                          final ObjectMapper objectMapper)
	{
		this.commandLine = commandLine;
		this.taskRegistry = taskRegistry;
		this.optionsBinder = new TaskOptionsBinder(objectMapper);
	}

	public int run(final String[] args)
	{
		if (isListCommandsRequest(args))
		{
			taskRegistry.descriptors().stream()
			            .map(descriptor -> descriptor.cli().commandName())
			            .sorted()
			            .forEach(System.out::println);
			return 0;
		}

		final ParseResult parseResult = commandLine.parseArgs(args);
		if (CommandLine.printHelpIfRequested(parseResult))
		{
			return 0;
		}
		if (parseResult.subcommand() == null)
		{
			commandLine.usage(System.out);
			return 0;
		}

		final TaskDescriptor<?, ?, ?> descriptor =
				taskRegistry.findByName(parseResult.subcommand().commandSpec().name())
				            .orElseThrow(() -> new IllegalArgumentException(
						            "Unknown task: " + parseResult.subcommand().commandSpec().name()));
		final Map<String, Object> parsedValues = new LinkedHashMap<>();
		for (final TaskOptionDescriptor option : descriptor.cli().options())
		{
			parsedValues.put(option.cliName(), parseResult.subcommand().matchedOptionValue(option.cliName(), null));
		}

		final Map<String, Object> canonicalValues = optionsBinder.cliValues(descriptor.cli().options(), parsedValues);
		final Object mandatoryOptions = optionsBinder.bind(
				descriptor.mandatoryOptionsType(),
				descriptor.cli().options(),
				canonicalValues,
				true);
		final Object optionalOptions = optionsBinder.bind(
				descriptor.optionalOptionsType(),
				descriptor.cli().options(),
				canonicalValues,
				false);
		final Object output = execute(descriptor, mandatoryOptions, optionalOptions);
		System.out.println(renderCli(descriptor, output));
		return 0;
	}

	@SuppressWarnings("unchecked")
	private static <O, MO, OO> Object execute(final TaskDescriptor<?, ?, ?> descriptor,
	                                          final Object mandatoryOptions,
	                                          final Object optionalOptions)
	{
		return ((TaskDescriptor<O, MO, OO>) descriptor).facade().execute((MO) mandatoryOptions, (OO) optionalOptions);
	}

	@SuppressWarnings("unchecked")
	private static <O> String renderCli(final TaskDescriptor<?, ?, ?> descriptor, final Object output)
	{
		return ((TaskDescriptor<O, ?, ?>) descriptor).cli().outputRenderer().render((O) output);
	}

	private static boolean isListCommandsRequest(final String[] args)
	{
		return args.length == 1 && Arrays.asList("--list-commands", "list-commands").contains(args[0]);
	}
}
