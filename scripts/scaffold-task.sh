#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  ./scripts/scaffold-task.sh <implementation-path> <task-name> <mandatory-fields> <optional-fields> [output-type]

Arguments:
  implementation-path  Path under src/main/java/.../implementations, for example: file/dat/create
  task-name            Task name used for CLI and REST, for example: create-dat-file
  mandatory-fields     Comma-separated fields as name:type, for example: fileName:String,text:String
  optional-fields      Comma-separated fields as name:type, or - for none, for example: upperCase:boolean,overwrite:boolean
  output-type          Optional. Defaults to String

Examples:
  ./scripts/scaffold-task.sh file/dat/create create-dat-file "fileName:String,text:String" "upperCase:boolean,overwrite:boolean"
  ./scripts/scaffold-task.sh text/reverse reverse-text "text:String" - String
EOF
}

if [[ $# -lt 4 || $# -gt 5 ]]; then
  usage
  exit 1
fi

IMPLEMENTATION_PATH="$1"
TASK_NAME="$2"
MANDATORY_FIELDS="$3"
OPTIONAL_FIELDS="$4"
OUTPUT_TYPE="${5:-String}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
SRC_ROOT="$REPO_ROOT/src/main/java/de/gupta/automation/task/implementations/$IMPLEMENTATION_PATH"
DOMAIN_DIR="$SRC_ROOT/domain"
FRAMEWORK_DIR="$SRC_ROOT/framework"

DOMAIN_PACKAGE="de.gupta.automation.task.implementations.${IMPLEMENTATION_PATH//\//.}.domain"
FRAMEWORK_PACKAGE="de.gupta.automation.task.implementations.${IMPLEMENTATION_PATH//\//.}.framework"

trim() {
  local value="$1"
  value="${value#"${value%%[![:space:]]*}"}"
  value="${value%"${value##*[![:space:]]}"}"
  printf '%s' "$value"
}

to_pascal_case() {
  local input="$1"
  local cleaned
  cleaned="$(printf '%s' "$input" | sed -E 's/[^[:alnum:]]+/ /g')"
  local word
  local result=""
  for word in $cleaned; do
    result+="${word^}"
  done
  printf '%s' "$result"
}

camel_to_kebab() {
  local input="$1"
  printf '%s' "$input" | sed -E 's/([a-z0-9])([A-Z])/\1-\L\2/g' | tr '[:upper:]' '[:lower:]'
}

camel_to_words() {
  local input="$1"
  printf '%s' "$input" | sed -E 's/([a-z0-9])([A-Z])/\1 \2/g' | tr '[:upper:]' '[:lower:]'
}

parse_fields() {
  local source="$1"
  local -n names_ref="$2"
  local -n types_ref="$3"
  names_ref=()
  types_ref=()

  if [[ -z "$source" || "$source" == "-" ]]; then
    return 0
  fi

  local raw_field
  IFS=',' read -r -a raw_fields <<< "$source"
  for raw_field in "${raw_fields[@]}"; do
    raw_field="$(trim "$raw_field")"
    [[ -z "$raw_field" ]] && continue
    if [[ "$raw_field" != *:* ]]; then
      echo "Invalid field definition: $raw_field"
      echo "Expected name:type"
      exit 1
    fi

    local field_name
    local field_type
    field_name="$(trim "${raw_field%%:*}")"
    field_type="$(trim "${raw_field#*:}")"

    if [[ -z "$field_name" || -z "$field_type" ]]; then
      echo "Invalid field definition: $raw_field"
      exit 1
    fi

    names_ref+=("$field_name")
    types_ref+=("$field_type")
  done
}

join_record_components() {
  local -n names_ref="$1"
  local -n types_ref="$2"
  local result=""
  local index
  for index in "${!names_ref[@]}"; do
    [[ -n "$result" ]] && result+=", "
    result+="${types_ref[$index]} ${names_ref[$index]}"
  done
  printf '%s' "$result"
}

join_input_components() {
  local result=""
  local index
  for index in "${!mandatory_names[@]}"; do
    [[ -n "$result" ]] && result+=", "
    result+="${mandatory_types[$index]} ${mandatory_names[$index]}"
  done
  for index in "${!optional_names[@]}"; do
    [[ -n "$result" ]] && result+=", "
    result+="${optional_types[$index]} ${optional_names[$index]}"
  done
  printf '%s' "$result"
}

assembler_arguments() {
  local result=""
  local index
  for index in "${!mandatory_names[@]}"; do
    [[ -n "$result" ]] && result+=", "
    result+="mandatoryOptions.${mandatory_names[$index]}()"
  done
  for index in "${!optional_names[@]}"; do
    [[ -n "$result" ]] && result+=", "
    result+="optionalOptions.${optional_names[$index]}()"
  done
  printf '%s' "$result"
}

description_for_field() {
  local field_name="$1"
  local words
  words="$(camel_to_words "$field_name")"
  printf '%s.' "${words^}"
}

registration_option_lines() {
  local mandatory_flag="$1"
  local -n names_ref="$2"
  local -n types_ref="$3"
  local line_prefix
  if [[ "$mandatory_flag" == "true" ]]; then
    line_prefix=".mandatoryOption"
  else
    line_prefix=".optionalOption"
  fi

  local index
  for index in "${!names_ref[@]}"; do
    local field_name="${names_ref[$index]}"
    local field_type="${types_ref[$index]}"
    local cli_name="--$(camel_to_kebab "$field_name")"
    local description
    description="$(description_for_field "$field_name")"

    printf '                %s("%s", %s.class, option -> option\n' "$line_prefix" "$field_name" "$field_type"
    printf '                        .cliName("%s")\n' "$cli_name"
    printf '                        .restName("%s")\n' "$field_name"
    printf '                        .description("%s")' "$description"

    if [[ "$mandatory_flag" == "false" && "$field_type" == "boolean" ]]; then
      printf '\n                        .documentedDefaultValue("false"))\n'
    else
      printf ')\n'
    fi
  done
}

BASE_NAME="$(to_pascal_case "$TASK_NAME")"
MANDATORY_CLASS="${BASE_NAME}MandatoryOptions"
OPTIONAL_CLASS="${BASE_NAME}OptionalOptions"
INPUT_CLASS="${BASE_NAME}Input"
TASK_CLASS="${BASE_NAME}Task"
ASSEMBLER_CLASS="${BASE_NAME}InputAssembler"
VALIDATOR_CLASS="${BASE_NAME}Validator"
REGISTRATION_CLASS="${BASE_NAME}Registration"

parse_fields "$MANDATORY_FIELDS" mandatory_names mandatory_types
parse_fields "$OPTIONAL_FIELDS" optional_names optional_types

if [[ ${#mandatory_names[@]} -eq 0 ]]; then
  echo "At least one mandatory field is required."
  exit 1
fi

mkdir -p "$DOMAIN_DIR" "$FRAMEWORK_DIR"

FILES=(
  "$DOMAIN_DIR/$MANDATORY_CLASS.java"
  "$DOMAIN_DIR/$OPTIONAL_CLASS.java"
  "$DOMAIN_DIR/$INPUT_CLASS.java"
  "$DOMAIN_DIR/$TASK_CLASS.java"
  "$DOMAIN_DIR/$ASSEMBLER_CLASS.java"
  "$DOMAIN_DIR/$VALIDATOR_CLASS.java"
  "$FRAMEWORK_DIR/$REGISTRATION_CLASS.java"
)

for file in "${FILES[@]}"; do
  if [[ -e "$file" ]]; then
    echo "Refusing to overwrite existing file: $file"
    exit 1
  fi
done

MANDATORY_COMPONENTS="$(join_record_components mandatory_names mandatory_types)"
OPTIONAL_COMPONENTS="$(join_record_components optional_names optional_types)"
INPUT_COMPONENTS="$(join_input_components)"
ASSEMBLER_ARGS="$(assembler_arguments)"

cat > "$DOMAIN_DIR/$MANDATORY_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

public record $MANDATORY_CLASS($MANDATORY_COMPONENTS)
{
}
EOF

cat > "$DOMAIN_DIR/$OPTIONAL_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

public record $OPTIONAL_CLASS($OPTIONAL_COMPONENTS)
{
}
EOF

cat > "$DOMAIN_DIR/$INPUT_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

public record $INPUT_CLASS($INPUT_COMPONENTS)
{
}
EOF

cat > "$DOMAIN_DIR/$TASK_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

import de.gupta.automation.task.framework.domain.port.TaskFunction;

public final class $TASK_CLASS implements TaskFunction<$INPUT_CLASS, $OUTPUT_TYPE>
{
	@Override
	public $OUTPUT_TYPE execute(final $INPUT_CLASS input)
	{
		throw new UnsupportedOperationException("Implement task logic.");
	}
}
EOF

cat > "$DOMAIN_DIR/$ASSEMBLER_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

import de.gupta.automation.task.framework.domain.port.TaskInputAssembler;

public final class $ASSEMBLER_CLASS
		implements TaskInputAssembler<$INPUT_CLASS, $MANDATORY_CLASS, $OPTIONAL_CLASS>
{
	@Override
	public $INPUT_CLASS assemble(final $MANDATORY_CLASS mandatoryOptions,
	                             final $OPTIONAL_CLASS optionalOptions)
	{
		return new $INPUT_CLASS($ASSEMBLER_ARGS);
	}
}
EOF

cat > "$DOMAIN_DIR/$VALIDATOR_CLASS.java" <<EOF
package $DOMAIN_PACKAGE;

import de.gupta.automation.task.framework.domain.port.TaskValidator;

public final class $VALIDATOR_CLASS implements TaskValidator<$MANDATORY_CLASS, $OPTIONAL_CLASS>
{
	@Override
	public void validateMandatory(final $MANDATORY_CLASS mandatoryOptions)
	{
		// TODO: implement mandatory option validation
	}

	@Override
	public void validateOptional(final $OPTIONAL_CLASS optionalOptions)
	{
		// TODO: implement optional option validation
	}
}
EOF

{
  cat <<EOF
package $FRAMEWORK_PACKAGE;

import de.gupta.automation.task.framework.domain.port.TaskDescriptor;
import de.gupta.automation.task.framework.registry.TaskDescriptorBuilder;
import $DOMAIN_PACKAGE.$ASSEMBLER_CLASS;
import $DOMAIN_PACKAGE.$INPUT_CLASS;
import $DOMAIN_PACKAGE.$MANDATORY_CLASS;
import $DOMAIN_PACKAGE.$OPTIONAL_CLASS;
import $DOMAIN_PACKAGE.$TASK_CLASS;
import $DOMAIN_PACKAGE.$VALIDATOR_CLASS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration(proxyBeanMethods = false)
public class $REGISTRATION_CLASS
{
	@Bean
	TaskDescriptor<$OUTPUT_TYPE, $MANDATORY_CLASS, $OPTIONAL_CLASS> ${TASK_NAME//-}Descriptor()
	{
		return TaskDescriptorBuilder.task("$TASK_NAME")
				.version("1")
				.types($INPUT_CLASS.class, $OUTPUT_TYPE.class, $MANDATORY_CLASS.class, $OPTIONAL_CLASS.class)
				.function(new $TASK_CLASS())
				.assembler(new $ASSEMBLER_CLASS())
				.validator(new $VALIDATOR_CLASS())
EOF
  registration_option_lines true mandatory_names mandatory_types
  registration_option_lines false optional_names optional_types
  cat <<EOF
                .cli(cli -> cli
                        .commandName("$TASK_NAME")
                        .description("TODO: describe $TASK_NAME.")
EOF
  if [[ "$OUTPUT_TYPE" == "String" ]]; then
    cat <<EOF
                        .outputRenderer(output -> output))
                .rest(rest -> rest
                        .path("/api/tasks/$TASK_NAME/execute")
                        .description("TODO: describe $TASK_NAME.")
                        .produces(MediaType.TEXT_PLAIN_VALUE)
                        .outputRenderer(output -> output))
EOF
  else
    cat <<EOF
                        .outputRenderer(output -> String.valueOf(output)))
                .rest(rest -> rest
                        .path("/api/tasks/$TASK_NAME/execute")
                        .description("TODO: describe $TASK_NAME.")
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .outputRenderer(output -> output))
EOF
  fi
  cat <<'EOF'
                .build();
	}
}
EOF
} > "$FRAMEWORK_DIR/$REGISTRATION_CLASS.java"

echo "Scaffolded task: $TASK_NAME"
printf 'Created files:\n'
for file in "${FILES[@]}"; do
  printf ' - %s\n' "$file"
done

if [[ "$OUTPUT_TYPE" != "String" ]]; then
  echo "Note: $OUTPUT_TYPE was referenced but not generated. Create that output type if it does not already exist."
fi
