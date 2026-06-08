package de.gupta.automation.task.implementations.text.print.manual.api;

public interface PrintTextApplicationService
{
	String execute(final String text, final int repeatCount, final String prefix, final boolean upperCase);
}