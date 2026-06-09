package de.gupta.automation.task.framework.aot;

import de.gupta.automation.task.framework.cli.FrameworkCliStatusListener;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.ClassPathResource;

public final class FrameworkCliRuntimeHints implements RuntimeHintsRegistrar
{
	@Override
	public void registerHints(final RuntimeHints hints, final ClassLoader classLoader)
	{
		hints.resources().registerResource(new ClassPathResource("logback-cli.xml"));
		hints.reflection().registerType(FrameworkCliStatusListener.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
	}
}
