package de.gupta.automation.task.printtext.internal;

import org.springframework.stereotype.Component;

import de.gupta.automation.task.domain.port.TaskInputAssembler;

@Component
final class PrintTextInputAssembler
		implements TaskInputAssembler<PrintTextInput, PrintTextMandatoryOptions, PrintTextOptionalOptions>
{
	private static final String DEFAULT_PREFIX = "OUTPUT: ";

	PrintTextInputAssembler()
	{
	}

	@Override
	public PrintTextInput assemble(final PrintTextMandatoryOptions mandatoryOptions,
	                               final PrintTextOptionalOptions optionalOptions)
	{
		final String prefix = optionalOptions.prefix() == null || optionalOptions.prefix().isBlank()
				? DEFAULT_PREFIX
				: optionalOptions.prefix();
		return new PrintTextInput(mandatoryOptions.text(), mandatoryOptions.repeatCount(), prefix,
				optionalOptions.upperCase());
	}
}
