package de.gupta.automation.task.printtext.api;

public interface PrintTextApplicationService
{
	String execute(final String text, final int repeatCount, final String prefix, final boolean upperCase);
}