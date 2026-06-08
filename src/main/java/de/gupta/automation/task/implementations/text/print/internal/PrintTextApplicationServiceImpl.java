package de.gupta.automation.task.implementations.text.print.internal;

import org.springframework.stereotype.Service;

import de.gupta.automation.task.template.domain.port.TaskFacade;
import de.gupta.automation.task.implementations.text.print.api.PrintTextApplicationService;

@Service
final class PrintTextApplicationServiceImpl implements PrintTextApplicationService
{
	private final TaskFacade<String, PrintTextMandatoryOptions, PrintTextOptionalOptions> facade;

	PrintTextApplicationServiceImpl(final PrintTextInputAssembler assembler,
	                                final PrintTextTask task,
	                                final PrintTextValidator validator)
	{
		this.facade = TaskFacade.create(assembler, task, validator);
	}

	@Override
	public String execute(final String text, final int repeatCount, final String prefix, final boolean upperCase)
	{
		return facade.execute(
				new PrintTextMandatoryOptions(text, repeatCount),
				new PrintTextOptionalOptions(prefix, upperCase));
	}
}