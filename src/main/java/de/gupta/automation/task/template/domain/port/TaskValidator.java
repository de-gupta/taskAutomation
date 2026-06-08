package de.gupta.automation.task.template.domain.port;

public interface TaskValidator<MO, OO>
{
	void validateMandatory(MO mandatoryOptions);

	void validateOptional(OO optionalOptions);
}