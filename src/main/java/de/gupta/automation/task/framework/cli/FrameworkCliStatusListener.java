package de.gupta.automation.task.framework.cli;

import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;

public final class FrameworkCliStatusListener implements StatusListener
{
	@Override
	public void addStatusEvent(final Status status)
	{
		// Suppress logback bootstrap status output for the CLI runtime.
	}
}
