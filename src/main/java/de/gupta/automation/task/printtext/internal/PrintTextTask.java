package de.gupta.automation.task.printtext.internal;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import de.gupta.automation.task.domain.port.TaskFunction;

@Component
final class PrintTextTask implements TaskFunction<PrintTextInput, String>
{
	PrintTextTask()
	{
	}

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
