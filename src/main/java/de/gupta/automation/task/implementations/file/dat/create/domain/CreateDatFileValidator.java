package de.gupta.automation.task.implementations.file.dat.create.domain;

import de.gupta.automation.task.framework.domain.port.TaskValidator;

public final class CreateDatFileValidator
		implements TaskValidator<CreateDatFileMandatoryOptions, CreateDatFileOptionalOptions>
{
	@Override
	public void validateMandatory(final CreateDatFileMandatoryOptions mandatoryOptions)
	{
		if (mandatoryOptions == null)
		{
			throw new IllegalArgumentException("Mandatory options are required.");
		}
		if (mandatoryOptions.fileName() == null || mandatoryOptions.fileName().isBlank())
		{
			throw new IllegalArgumentException("File name must not be blank.");
		}
		if (mandatoryOptions.text() == null || mandatoryOptions.text().isBlank())
		{
			throw new IllegalArgumentException("Text must not be blank.");
		}
	}

	@Override
	public void validateOptional(final CreateDatFileOptionalOptions optionalOptions)
	{
		if (optionalOptions == null)
		{
			throw new IllegalArgumentException("Optional options are required.");
		}
	}
}
