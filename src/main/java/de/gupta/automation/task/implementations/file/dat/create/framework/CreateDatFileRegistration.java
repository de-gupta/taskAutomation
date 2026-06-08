package de.gupta.automation.task.implementations.file.dat.create.framework;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskDescriptorBuilder;
import de.gupta.automation.task.implementations.file.dat.create.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration(proxyBeanMethods = false)
public class CreateDatFileRegistration
{
	@Bean
	TaskDescriptor<String, CreateDatFileMandatoryOptions, CreateDatFileOptionalOptions> createDatFileDescriptor()
	{
		return TaskDescriptorBuilder.task("create-dat-file")
		                            .version("1")
		                            .types(
											CreateDatFileInput.class,
											String.class,
											CreateDatFileMandatoryOptions.class,
											CreateDatFileOptionalOptions.class)
		                            .function(new CreateDatFileTask())
		                            .assembler(new CreateDatFileInputAssembler())
		                            .validator(new CreateDatFileValidator())
		                            .mandatoryOption("fileName", String.class, option -> option
											.cliName("--file-name")
				                            .restName("fileName")
				                            .description("Target file name. A .dat extension is added if missing."))
		                            .mandatoryOption("text", String.class, option -> option
											.cliName("--text")
				                            .restName("text")
				                            .description("Text to write into the file."))
		                            .optionalOption("upperCase", boolean.class, option -> option
											.cliName("--uppercase")
				                            .restName("upperCase")
				                            .description("Uppercase the text before writing it.")
				                            .documentedDefaultValue("false"))
		                            .optionalOption("overwrite", boolean.class, option -> option
											.cliName("--overwrite")
				                            .restName("overwrite")
				                            .description("Overwrite the file if it already exists.")
				                            .documentedDefaultValue("false"))
		                            .cli(cli -> cli
											.commandName("create-dat-file")
				                            .description("Create a .dat file and return its full path.")
				                            .outputRenderer(output -> output))
		                            .rest(rest -> rest
											.path("/api/tasks/create-dat-file/execute")
				                            .description("Create a .dat file and return its full path.")
				                            .produces(MediaType.TEXT_PLAIN_VALUE)
				                            .outputRenderer(output -> output))
		                            .build();
	}
}