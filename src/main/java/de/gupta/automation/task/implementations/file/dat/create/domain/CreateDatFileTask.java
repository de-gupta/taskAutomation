package de.gupta.automation.task.implementations.file.dat.create.domain;

import de.gupta.automation.task.framework.domain.port.TaskFunction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class CreateDatFileTask implements TaskFunction<CreateDatFileInput, String>
{
	@Override
	public String execute(final CreateDatFileInput input)
	{
		try
		{
			if (input.filePath().getParent() != null)
			{
				Files.createDirectories(input.filePath().getParent());
			}

			final String text = input.upperCase() ? input.text().toUpperCase() : input.text();
			if (input.overwrite())
			{
				Files.writeString(
						input.filePath(),
						text,
						StandardCharsets.UTF_8,
						StandardOpenOption.CREATE,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.WRITE);
			}
			else
			{
				Files.writeString(
						input.filePath(),
						text,
						StandardCharsets.UTF_8,
						StandardOpenOption.CREATE_NEW,
						StandardOpenOption.WRITE);
			}
			return input.filePath().toString();
		}
		catch (final IOException exception)
		{
			throw new IllegalStateException("Failed to create .dat file: " + input.filePath(), exception);
		}
	}
}
