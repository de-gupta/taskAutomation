package de.gupta.automation.task.implementations.text.print.domain;

import de.gupta.automation.task.framework.domain.port.TaskFunction;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PrintTextTask implements TaskFunction<PrintTextInput, String>
{
	@Override
	public String execute(final PrintTextInput input)
	{
		final String text = input.upperCase() ? input.text().toUpperCase() : input.text();
		final String line = input.prefix() + text;

		return IntStream.range(0, input.repeatCount())
		                .mapToObj(index -> line)
		                .collect(Collectors.joining(System.lineSeparator()));
	}
}