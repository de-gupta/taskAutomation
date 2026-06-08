package de.gupta.automation.task.printtext.internal;

import de.gupta.automation.task.domain.port.TaskDescriptor;

final class PrintTextTaskDescriptor
		implements TaskDescriptor<PrintTextInput, String, PrintTextMandatoryOptions, PrintTextOptionalOptions>
{
	PrintTextTaskDescriptor()
	{
	}

	@Override
	public String name()
	{
		return "print-text";
	}

	@Override
	public String version()
	{
		return "1";
	}

	@Override
	public Class<PrintTextInput> inputType()
	{
		return PrintTextInput.class;
	}

	@Override
	public Class<String> outputType()
	{
		return String.class;
	}

	@Override
	public Class<PrintTextMandatoryOptions> mandatoryOptionsType()
	{
		return PrintTextMandatoryOptions.class;
	}

	@Override
	public Class<PrintTextOptionalOptions> optionalOptionsType()
	{
		return PrintTextOptionalOptions.class;
	}
}
