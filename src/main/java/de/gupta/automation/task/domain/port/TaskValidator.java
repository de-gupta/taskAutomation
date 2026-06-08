package de.gupta.automation.task.domain.port;

public interface TaskValidator<MO, OO>
{
	void validateMandatory(MO mandatoryOptions);

	void validateOptional(OO optionalOptions);
}
