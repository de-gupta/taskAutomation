# Task Automation

`taskAutomation` is a small framework for exposing registered tasks through:

- a CLI
- a REST API
- OpenAPI / Swagger documentation

The repository currently ships with two example tasks:

- `print-text`
- `create-dat-file`

The important user-facing idea is simple:

- tasks are registered once
- the framework exposes them automatically on CLI and REST
- Swagger documentation is generated from task metadata

You do not need to hand-write a CLI command or REST controller for each task.

## Add A New Task

This is the most important workflow in the repository.

To add a new task, a developer should write:

1. domain types for the task
2. the task logic
3. the input assembler
4. the validator
5. one registration class

That is all.

The framework then provides:

- a CLI command
- a REST endpoint
- OpenAPI / Swagger documentation
- CLI wrapper installation for the command

### What You Need To Write

For a task named `create-dat-file`, the developer writes these classes.

#### 1. `<TaskName>MandatoryOptions`

Example:

- [CreateDatFileMandatoryOptions.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileMandatoryOptions.java)

Purpose:

- contains the required user inputs
- defines what the task cannot run without

For `create-dat-file`, those are:

- `fileName`
- `text`

Reasoning:

- required inputs should be explicit
- CLI and REST both bind into the same required-options type
- validation can treat required and optional data differently

#### 2. `<TaskName>OptionalOptions`

Example:

- [CreateDatFileOptionalOptions.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileOptionalOptions.java)

Purpose:

- contains the optional user inputs
- carries toggles and non-essential parameters

For `create-dat-file`, those are:

- `upperCase`
- `overwrite`

Reasoning:

- optional values often have defaults
- keeping them separate makes defaulting and docs clearer
- the framework can document them as optional in CLI and Swagger

#### 3. `<TaskName>Input`

Example:

- [CreateDatFileInput.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileInput.java)

Purpose:

- defines the canonical input for the task logic
- contains the normalized values the task actually executes on

For `create-dat-file`, that includes:

- resolved absolute file path
- text to write
- normalized boolean flags

Reasoning:

- the business task should not care about raw transport input
- normalization belongs before execution
- this keeps task logic clean and deterministic

#### 4. `<TaskName>Task`

Example:

- [CreateDatFileTask.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileTask.java)

Purpose:

- contains the actual business behavior
- implements `TaskFunction<I, O>`

For `create-dat-file`, it:

- creates the target file
- writes the text
- respects the overwrite flag
- returns the full path

Reasoning:

- this should be the smallest, clearest business unit
- no CLI parsing
- no REST concerns
- no defaulting logic

#### 5. `<TaskName>InputAssembler`

Example:

- [CreateDatFileInputAssembler.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileInputAssembler.java)

Purpose:

- converts `MandatoryOptions` and `OptionalOptions` into canonical `Input`
- performs normalization and defaulting

For `create-dat-file`, it:

- ensures the file name ends with `.dat`
- resolves the path to an absolute normalized path
- combines required and optional values into one canonical input

Reasoning:

- this is the right place for defaults and normalization
- keeping it separate prevents transport-specific hacks
- both CLI and REST share the same assembly logic

#### 6. `<TaskName>Validator`

Example:

- [CreateDatFileValidator.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/domain/CreateDatFileValidator.java)

Purpose:

- validates required and optional values before execution
- implements `TaskValidator<MO, OO>`

For `create-dat-file`, it checks:

- file name is present
- text is present

Reasoning:

- validation should happen once in the canonical pipeline
- validation should not be duplicated in CLI and REST
- semantic task rules belong with the task, not the transport

#### 7. `<TaskName>Registration`

Example:

- [CreateDatFileRegistration.java](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/src/main/java/de/gupta/automation/task/implementations/file/dat/create/framework/CreateDatFileRegistration.java)

Purpose:

- registers the task with the framework
- creates a `TaskDescriptor`
- declares CLI and REST metadata

This is where the developer defines:

- task name
- version
- `Input`, `Output`, `MandatoryOptions`, `OptionalOptions` types
- task function, assembler, and validator
- CLI option names and descriptions
- REST field names and endpoint path
- documented defaults
- output rendering

Reasoning:

- this is the one framework-facing class the task needs
- it keeps transport metadata out of the business classes
- the framework uses it to expose the task everywhere

### Recommended Package Structure

For a task named `create-dat-file`, use:

- `src/main/java/.../implementations/file/dat/create/domain`
- `src/main/java/.../implementations/file/dat/create/framework`

Put these classes in `domain`:

- `CreateDatFileMandatoryOptions`
- `CreateDatFileOptionalOptions`
- `CreateDatFileInput`
- `CreateDatFileTask`
- `CreateDatFileInputAssembler`
- `CreateDatFileValidator`

Put this class in `framework`:

- `CreateDatFileRegistration`

### What You Do Not Need To Write

You do not need to write:

- a CLI command class
- a REST controller
- a request DTO for Swagger
- a CLI launcher
- a REST launcher

The framework creates the exposed command, endpoint, and OpenAPI documentation from the registration metadata.

### What Happens After You Add The Registration Class

Once the registration bean is present and the app starts, the framework will:

- add the task to `--list-commands`
- expose a CLI command with the registered name
- expose a REST endpoint at the registered path
- include the task in Swagger / OpenAPI
- generate a shell wrapper for the command when you run the CLI installer

That is why the registration class is the key integration point.

## What You Get

Out of the box, this repository provides:

- a packaged CLI application
- a packaged REST application
- task-specific shell wrappers such as `print-text` and `create-dat-file`
- generated OpenAPI documentation for registered tasks

Current packaged artifacts:

- `target/taskAutomation-0.0.1-SNAPSHOT-cli.jar`
- `target/taskAutomation-0.0.1-SNAPSHOT-rest.jar`

## Current Example Task

The repository currently includes two registered tasks:

- `print-text`
- `create-dat-file`

It can:

- print text to the console
- repeat the output multiple times
- apply an optional prefix
- uppercase the text

### `print-text` options

- `--text <string>`: required
- `--repeat <int>`: required
- `--prefix <string>`: optional
- `--uppercase`: optional boolean flag

Behavior:

- `--repeat` must be greater than `0`
- if `--prefix` is omitted or blank, it defaults to `OUTPUT: `
- `--uppercase` is a flag, so use `--uppercase`, not `--uppercase true`

Examples:

```bash
print-text --text hello --repeat 2
```

```bash
print-text --text hello --repeat 2 --prefix "NOTE: "
```

```bash
print-text --text hello --repeat 2 --uppercase
```

```bash
print-text --text hello --repeat 2 --prefix "NOTE: " --uppercase
```

### `create-dat-file` options

- `--file-name <string>`: required
- `--text <string>`: required
- `--uppercase`: optional boolean flag
- `--overwrite`: optional boolean flag

Behavior:

- if `--file-name` does not end with `.dat`, the extension is added automatically
- `--uppercase` defaults to `false`
- `--overwrite` defaults to `false`
- output is the full absolute path of the created file

Examples:

```bash
create-dat-file --file-name ./demo --text "hello world"
```

```bash
create-dat-file --file-name ./demo.dat --text "hello world" --uppercase
```

```bash
create-dat-file --file-name ./demo.dat --text "hello world" --overwrite
```

## Requirements

To build and run the project, you need:

- Java
- Maven

For the CLI wrapper scripts, you also need:

- `bash` on Linux/macOS
- PowerShell on Windows

## Quick Start

### Bash / Linux / macOS

The fastest path is:

```bash
./scripts/bootstrap-cli.sh
```

This script:

- builds the project
- packages the CLI jar
- installs wrapper commands into `~/.local/bin` by default

If `~/.local/bin` is not already on your `PATH`, add:

```bash
export PATH="$HOME/.local/bin:$PATH"
```

Then you can run:

```bash
print-text --text hello --repeat 2
create-dat-file --file-name ./demo --text "hello world"
```

If you want a different install location:

```bash
TASK_AUTOMATION_INSTALL_DIR=/your/bin ./scripts/bootstrap-cli.sh
```

### Windows / PowerShell

First build the project:

```powershell
mvn -q -DskipTests package
```

Then install the CLI wrappers:

```powershell
.\scripts\install-cli.ps1 -CliJarPath .\target\taskAutomation-0.0.1-SNAPSHOT-cli.jar -InstallDir C:\Tools\task-automation
```

If `C:\Tools\task-automation` is on `PATH`, you can then run:

```powershell
print-text --text hello --repeat 2
create-dat-file --file-name .\demo --text "hello world"
```

## Build

To package the project:

```bash
mvn -q -DskipTests package
```

This produces:

- the plain jar: `target/taskAutomation-0.0.1-SNAPSHOT.jar`
- the CLI boot jar: `target/taskAutomation-0.0.1-SNAPSHOT-cli.jar`
- the REST boot jar: `target/taskAutomation-0.0.1-SNAPSHOT-rest.jar`

To run tests:

```bash
mvn -q test
```

## Running The CLI

You can run the CLI directly from the packaged jar even without installing wrappers.

### List available commands

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar --list-commands
```

Current output should include:

```text
create-dat-file
print-text
```

### Run through the generic dispatcher

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar print-text --text hello --repeat 2
```

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar create-dat-file --file-name ./demo --text "hello world"
```

### Show help

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar --help
```

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar print-text --help
```

### Run through installed wrappers

If you installed wrappers, you can use:

```bash
tasks print-text --text hello --repeat 2
```

or directly:

```bash
print-text --text hello --repeat 2
create-dat-file --file-name ./demo --text "hello world"
```

## CLI Wrapper Scripts

This repository includes wrapper installation scripts so the CLI behaves like a normal shell command.

### Bash installer

[scripts/install-cli.sh](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/scripts/install-cli.sh)

Usage:

```bash
./scripts/install-cli.sh ./target/taskAutomation-0.0.1-SNAPSHOT-cli.jar ~/.local/bin
```

It creates:

- `tasks`
- one wrapper per registered CLI task, for example `print-text`

### Bash bootstrap installer

[scripts/bootstrap-cli.sh](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/scripts/bootstrap-cli.sh)

Usage:

```bash
./scripts/bootstrap-cli.sh
```

It is the lazy one-step installer:

- builds the CLI jar
- finds the correct jar version automatically
- installs the wrappers

### PowerShell installer

[scripts/install-cli.ps1](E:/Projects/Professional/OpenSource/java/de-gupta/task.automation/taskAutomation/scripts/install-cli.ps1)

Usage:

```powershell
.\scripts\install-cli.ps1 -CliJarPath .\target\taskAutomation-0.0.1-SNAPSHOT-cli.jar -InstallDir C:\Tools\task-automation
```

It creates:

- `tasks.cmd`
- one wrapper per registered CLI task, for example `print-text.cmd`

## What The CLI Wrappers Actually Do

The wrappers do not install native binaries.

They simply call:

```text
java -jar <cli-jar> ...
```

That means:

- they survive shell restarts and system restarts
- they behave like normal commands once installed on `PATH`
- they still pay JVM startup cost on each run

So yes, you will notice some startup lag.

That is expected for the current jar-based setup.

## Running The REST API

To run the REST application:

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-rest.jar
```

Or with Maven:

```bash
mvn -DskipTests "-Dspring-boot.run.main-class=de.gupta.automation.task.framework.rest.FrameworkRestApplication" spring-boot:run
```

Once the REST app is running, registered tasks are available over HTTP.

For `print-text`, the endpoint is:

```text
POST /api/tasks/print-text/execute
```

Example request body:

```json
{
  "text": "hello",
  "repeatCount": 2,
  "prefix": "NOTE: ",
  "upperCase": true
}
```

Example response:

```text
NOTE: HELLO
NOTE: HELLO
```

For `create-dat-file`, the endpoint is:

```text
POST /api/tasks/create-dat-file/execute
```

Example request body:

```json
{
  "fileName": "./demo",
  "text": "hello world",
  "upperCase": true,
  "overwrite": false
}
```

Example response:

```text
/absolute/path/to/demo.dat
```

The REST endpoint exists only while the REST application is running.

## Swagger / OpenAPI

When the REST app is running, OpenAPI documentation is generated automatically from the registered task metadata.

Useful endpoints:

- `/v3/api-docs`
- `/swagger-ui/index.html`

For registered tasks such as `print-text` and `create-dat-file`, Swagger should show:

- the concrete task paths such as `/api/tasks/print-text/execute` and `/api/tasks/create-dat-file/execute`
- task-specific request fields
- descriptions for those fields
- documented defaults where applicable

You do not need to hand-write a controller or DTO just to get Swagger hints.

## Adding More Tasks

As more tasks are registered, the framework will:

- expose them in the CLI command list
- generate additional shell wrappers during CLI install
- expose them through REST
- include them in OpenAPI / Swagger

The runtime also refuses to start if tasks clash on:

- task name
- CLI command name
- REST path

So collisions fail fast instead of producing ambiguous behavior.

## Common Workflows

### Build and install CLI wrappers on bash

```bash
./scripts/bootstrap-cli.sh
```

### Build and run the CLI jar directly

```bash
mvn -q -DskipTests package
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar print-text --text hello --repeat 2
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar create-dat-file --file-name ./demo --text "hello world"
```

### Build and run the REST jar

```bash
mvn -q -DskipTests package
java -jar target/taskAutomation-0.0.1-SNAPSHOT-rest.jar
```

### Inspect available CLI commands

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar --list-commands
```

## Current Limitations

- CLI commands are jar-based, not native executables
- each CLI run starts a fresh JVM process, so startup is not instant
- REST is available only while the REST application is running
- the repository currently ships with two example tasks: `print-text` and `create-dat-file`

## Summary

Use this repository if you want:

- task registration once
- automatic CLI exposure
- automatic REST exposure
- automatic OpenAPI generation
- optional shell wrapper installation for CLI commands

The quickest way to get productive on bash is:

```bash
./scripts/bootstrap-cli.sh
print-text --text hello --repeat 2
create-dat-file --file-name ./demo --text "hello world"
```
