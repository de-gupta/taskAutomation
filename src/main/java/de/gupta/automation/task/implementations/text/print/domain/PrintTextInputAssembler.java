package de.gupta.automation.task.implementations.text.print.domain;

import de.gupta.automation.task.framework.domain.port.TaskInputAssembler;

public final class PrintTextInputAssembler
		implements TaskInputAssembler<PrintTextInput, PrintTextMandatoryOptions, PrintTextOptionalOptions>
{
	private static final String DEFAULT_PREFIX = "OUTPUT: ";

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
