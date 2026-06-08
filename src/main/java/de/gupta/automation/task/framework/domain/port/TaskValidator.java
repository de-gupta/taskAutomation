package de.gupta.automation.task.framework.domain.port;

public interface TaskValidator<MO, OO>
{
	void validateMandatory(MO mandatoryOptions);

	void validateOptional(OO optionalOptions);
}
