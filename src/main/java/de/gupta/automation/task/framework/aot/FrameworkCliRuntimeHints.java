package de.gupta.automation.task.framework.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;

public final class FrameworkCliRuntimeHints implements RuntimeHintsRegistrar
{
	@Override
	public void registerHints(final RuntimeHints hints, final ClassLoader classLoader)
	{
		hints.resources().registerResource(new ClassPathResource("logback-cli.xml"));
	}
}
