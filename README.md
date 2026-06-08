# Task Automation

`taskAutomation` is a small framework for exposing registered tasks through:

- a CLI
- a REST API
- OpenAPI / Swagger documentation

The repository currently ships with one example task:

- `print-text`

The important user-facing idea is simple:

- tasks are registered once
- the framework exposes them automatically on CLI and REST
- Swagger documentation is generated from task metadata

You do not need to hand-write a CLI command or REST controller for each task.

## What You Get

Out of the box, this repository provides:

- a packaged CLI application
- a packaged REST application
- task-specific shell wrappers such as `print-text`
- generated OpenAPI documentation for registered tasks

Current packaged artifacts:

- `target/taskAutomation-0.0.1-SNAPSHOT-cli.jar`
- `target/taskAutomation-0.0.1-SNAPSHOT-rest.jar`

## Current Example Task

The repository currently includes one registered task:

- `print-text`

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
print-text
```

### Run through the generic dispatcher

```bash
java -jar target/taskAutomation-0.0.1-SNAPSHOT-cli.jar print-text --text hello --repeat 2
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

The REST endpoint exists only while the REST application is running.

## Swagger / OpenAPI

When the REST app is running, OpenAPI documentation is generated automatically from the registered task metadata.

Useful endpoints:

- `/v3/api-docs`
- `/swagger-ui/index.html`

For `print-text`, Swagger should show:

- the concrete task path `/api/tasks/print-text/execute`
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
- the repository currently ships with one example task: `print-text`

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
```
