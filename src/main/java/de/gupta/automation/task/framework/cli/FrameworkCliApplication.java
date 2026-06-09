package de.gupta.automation.task.framework.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.net.URL;

@SpringBootApplication(proxyBeanMethods = false)
@ComponentScan(
		basePackages = {
				"de.gupta.automation.task.framework",
				"de.gupta.automation.task.implementations"
		}
)
public final class FrameworkCliApplication
{
	public static void main(final String[] args)
	{
		final URL logbackConfig = FrameworkCliApplication.class.getClassLoader().getResource("logback-cli.xml");
		if (logbackConfig != null)
		{
			System.setProperty("logback.configurationFile", logbackConfig.toString());
		}
		System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(FrameworkCliApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.logStartupInfo(false)
				.properties(
						"spring.main.banner-mode=off",
						"spring.main.log-startup-info=false",
						"logging.level.root=ERROR")
				.web(WebApplicationType.NONE)
				.run())
		{
			final FrameworkCliRunner runner = context.getBean(FrameworkCliRunner.class);
			final int exitCode = runner.run(args);
			if (exitCode != 0)
			{
				System.exit(exitCode);
			}
		}
	}

	private FrameworkCliApplication()
	{
	}
}
