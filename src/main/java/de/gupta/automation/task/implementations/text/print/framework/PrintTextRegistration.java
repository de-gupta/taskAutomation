package de.gupta.automation.task.implementations.text.print.framework;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskDescriptorBuilder;
import de.gupta.automation.task.implementations.text.print.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration(proxyBeanMethods = false)
public class PrintTextRegistration
{
	@Bean
	TaskDescriptor<String, PrintTextMandatoryOptions, PrintTextOptionalOptions> printTextDescriptor()
	{
		return TaskDescriptorBuilder.task("print-text")
		                            .version("1")
		                            .types(PrintTextInput.class, String.class, PrintTextMandatoryOptions.class,
				                            PrintTextOptionalOptions.class)
		                            .function(new PrintTextTask())
		                            .assembler(new PrintTextInputAssembler())
		                            .validator(new PrintTextValidator())
		                            .mandatoryOption("text", String.class, option -> option
											.cliName("--text")
				                            .restName("text")
				                            .description("Text to print."))
		                            .mandatoryOption("repeatCount", int.class, option -> option
											.cliName("--repeat")
				                            .restName("repeatCount")
				                            .description("Number of repetitions."))
		                            .optionalOption("prefix", String.class, option -> option
											.cliName("--prefix")
				                            .restName("prefix")
				                            .description("Optional prefix.")
				                            .documentedDefaultValue("OUTPUT: "))
		                            .optionalOption("upperCase", boolean.class, option -> option
											.cliName("--uppercase")
				                            .restName("upperCase")
				                            .description("Uppercase the text."))
		                            .cli(cli -> cli
											.commandName("print-text")
				                            .description("Print text to the console.")
				                            .outputRenderer(output -> output))
		                            .rest(rest -> rest
											.path("/api/tasks/print-text/execute")
				                            .description("Execute the print-text task.")
				                            .produces(MediaType.TEXT_PLAIN_VALUE)
				                            .outputRenderer(output -> output))
		                            .build();
	}
}