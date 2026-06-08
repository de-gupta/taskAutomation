package de.gupta.automation.task.implementations.file.dat.create.domain;

import java.nio.file.Path;

public record CreateDatFileInput(Path filePath, String text, boolean upperCase, boolean overwrite)
{
}
