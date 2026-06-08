package de.gupta.automation.task.implementations.text.print.internal;

import org.springframework.stereotype.Component;

import de.gupta.automation.task.template.domain.port.TaskValidator;

@Component
final class PrintTextValidator implements TaskValidator<PrintTextMandatoryOptions, PrintTextOptionalOptions>
{
	PrintTextValidator()
	{
	}

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