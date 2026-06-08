package de.gupta.automation.task.implementations.text.print.adapter.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "de.gupta.automation.task.printtext", proxyBeanMethods = false)
final class PrintTextRestApplication
{
	private PrintTextRestApplication()
	{
	}

	public static void main(final String[] args)
	{
		SpringApplication.run(PrintTextRestApplication.class, args);
	}
}