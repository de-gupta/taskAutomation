package de.gupta.automation.task.framework.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(proxyBeanMethods = false)
@ComponentScan(
		basePackages = {
				"de.gupta.automation.task.framework",
				"de.gupta.automation.task.implementations"
		},
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = "de\\.gupta\\.automation\\.task\\.implementations\\..*\\.manual\\..*"))
public final class FrameworkRestApplication
{
	public static void main(final String[] args)
	{
		SpringApplication.run(FrameworkRestApplication.class, args);
	}

	private FrameworkRestApplication()
	{
	}
}