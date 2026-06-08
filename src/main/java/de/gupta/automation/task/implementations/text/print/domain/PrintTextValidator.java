package de.gupta.automation.task.implementations.text.print.domain;

import de.gupta.automation.task.framework.domain.port.TaskValidator;

public final class PrintTextValidator implements TaskValidator<PrintTextMandatoryOptions, PrintTextOptionalOptions>
{
	@Override
	public void validateMandatory(final PrintTextMandatoryOptions mandatoryOptions)
	{
		if (mandatoryOptions == null)
		{
			throw new IllegalArgumentException("Mandatory options are required.");
		}
		if (mandatoryOptions.text() == null || mandatoryOptions.text().isBlank())
		{
			throw new IllegalArgumentException("Text must not be blank.");
		}
		if (mandatoryOptions.repeatCount() <= 0)
		{
			throw new IllegalArgumentException("Repeat count must be greater than zero.");
		}
	}

	@Override
	public void validateOptional(final PrintTextOptionalOptions optionalOptions)
	{
		if (optionalOptions == null)
		{
			throw new IllegalArgumentException("Optional options object is required.");
		}
		if (optionalOptions.prefix() != null && optionalOptions.prefix().contains(System.lineSeparator()))
		{
			throw new IllegalArgumentException("Prefix must be single-line text.");
		}
	}
}
