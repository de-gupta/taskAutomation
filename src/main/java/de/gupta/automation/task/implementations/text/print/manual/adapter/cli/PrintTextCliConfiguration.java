package de.gupta.automation.task.implementations.text.print.manual.adapter.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import picocli.CommandLine;

@Configuration(proxyBeanMethods = false)
final class PrintTextCliConfiguration
{
	@Bean
	CommandLine printTextCommandLine(final PrintTextCommand command)
	{
		return new CommandLine(command);
	}
}