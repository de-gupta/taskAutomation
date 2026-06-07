package de.gupta.automation.task.api.application;

public interface TaskAPI<API, O>
{
	O execute(API input);
}