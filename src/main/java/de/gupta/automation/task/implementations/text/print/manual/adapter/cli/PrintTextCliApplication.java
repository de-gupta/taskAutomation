package de.gupta.automation.task.implementations.text.print.manual.adapter.cli;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import picocli.CommandLine;

@SpringBootApplication(scanBasePackages = "de.gupta.automation.task.printtext", proxyBeanMethods = false)
final class PrintTextCliApplication
{
	private PrintTextCliApplication()
	{
	}

	public static void main(final String[] args)
	{
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(PrintTextCliApplication.class)
				.web(WebApplicationType.NONE)
				.run())
		{
			final CommandLine commandLine = context.getBean(CommandLine.class);
			final int exitCode = commandLine.execute(args);
			if (exitCode != 0)
			{
				System.exit(exitCode);
			}
		}
	}
}