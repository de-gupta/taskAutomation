package de.gupta.automation.task.implementations.file.dat.create.domain;

import de.gupta.automation.task.framework.domain.port.TaskInputAssembler;

import java.nio.file.Path;

public final class CreateDatFileInputAssembler
		implements TaskInputAssembler<CreateDatFileInput, CreateDatFileMandatoryOptions, CreateDatFileOptionalOptions>
{
	@Override
	public CreateDatFileInput assemble(final CreateDatFileMandatoryOptions mandatoryOptions,
	                                   final CreateDatFileOptionalOptions optionalOptions)
	{
		final Path filePath = normaliseFilePath(mandatoryOptions.fileName());
		return new CreateDatFileInput(filePath, mandatoryOptions.text(), optionalOptions.upperCase(),
				optionalOptions.overwrite());
	}

	private static Path normaliseFilePath(final String fileName)
	{
		final String trimmed = fileName.trim();
		final String datFileName = trimmed.toLowerCase().endsWith(".dat") ? trimmed : trimmed + ".dat";
		return Path.of(datFileName).toAbsolutePath().normalize();
	}
}
