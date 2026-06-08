package de.gupta.automation.task.implementations.text.print.adapter.cli;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import de.gupta.automation.task.implementations.text.print.api.PrintTextApplicationService;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Component
@Command(
		name = "print-text",
		mixinStandardHelpOptions = true,
		description = "Print text using the canonical task facade.")
final class PrintTextCommand implements Callable<Integer>
{
	private final PrintTextApplicationService applicationService;

	@Spec
	private CommandSpec spec;

	@Option(names = "--text", required = true, description = "Text to print.")
	private String text;

	@Option(names = "--repeat", required = true, description = "Number of repetitions.")
	private int repeatCount;

	@Option(names = "--prefix", defaultValue = "", description = "Optional text prefix.")
	private String prefix;

	@Option(names = "--uppercase", defaultValue = "false", description = "Uppercase the text before printing.")
	private boolean upperCase;

	PrintTextCommand(final PrintTextApplicationService applicationService)
	{
		this.applicationService = applicationService;
	}

	@Override
	public Integer call()
	{
		final String output = applicationService.execute(text, repeatCount, prefix, upperCase);
		spec.commandLine().getOut().println(output);
		return 0;
	}
}