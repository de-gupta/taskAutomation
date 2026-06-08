package de.gupta.automation.task.framework;

import de.gupta.automation.task.framework.cli.FrameworkCliApplication;
import de.gupta.automation.task.framework.cli.FrameworkCliRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FrameworkCliApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FrameworkCliRunnerTest
{
	@Autowired
	private FrameworkCliRunner frameworkCliRunner;

	@Test
	void shouldExecuteDescriptorDrivenCliCommand()
	{
		final PrintStream originalOut = System.out;
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try
		{
			System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
			final int exitCode = frameworkCliRunner.run(new String[]{
					"print-text",
					"--text", "hello",
					"--repeat", "2",
					"--prefix", "NOTE:",
					"--uppercase"
			});
			assertThat(exitCode).isZero();
			assertThat(output.toString(StandardCharsets.UTF_8))
					.contains("NOTE:HELLO")
					.contains(System.lineSeparator());
		}
		finally
		{
			System.setOut(originalOut);
		}
	}

	@Test
	void shouldListRegisteredCliCommands()
	{
		final PrintStream originalOut = System.out;
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try
		{
			System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
			final int exitCode = frameworkCliRunner.run(new String[]{"--list-commands"});
			assertThat(exitCode).isZero();
			assertThat(output.toString(StandardCharsets.UTF_8)).contains("print-text");
		}
		finally
		{
			System.setOut(originalOut);
		}
	}
}