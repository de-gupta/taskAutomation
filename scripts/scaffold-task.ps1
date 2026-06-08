param(
    [Parameter(Mandatory = $true)]
    [string]$ImplementationPath,

    [Parameter(Mandatory = $true)]
    [string]$TaskName,

    [Parameter(Mandatory = $true)]
    [string]$MandatoryFields,

    [Parameter(Mandatory = $true)]
    [string]$OptionalFields,

    [string]$OutputType = "String"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Trim-Value {
    param([string]$Value)
    return $Value.Trim()
}

function Convert-ToPascalCase {
    param([string]$Value)
    $parts = ($Value -replace '[^A-Za-z0-9]+', ' ').Split(' ', [System.StringSplitOptions]::RemoveEmptyEntries)
    return ($parts | ForEach-Object {
        if ($_.Length -eq 0) { return "" }
        $_.Substring(0, 1).ToUpperInvariant() + $_.Substring(1)
    }) -join ''
}

function Convert-CamelToKebabCase {
    param([string]$Value)
    return ([regex]::Replace($Value, '([a-z0-9])([A-Z])', '$1-$2')).ToLowerInvariant()
}

function Convert-CamelToWords {
    param([string]$Value)
    return ([regex]::Replace($Value, '([a-z0-9])([A-Z])', '$1 $2')).ToLowerInvariant()
}

function Parse-Fields {
    param([string]$Source)

    $fields = @()
    if ([string]::IsNullOrWhiteSpace($Source) -or $Source -eq "-") {
        return $fields
    }

    foreach ($rawField in $Source.Split(',')) {
        $field = $rawField.Trim()
        if ([string]::IsNullOrWhiteSpace($field)) {
            continue
        }

        $parts = $field.Split(':', 2)
        if ($parts.Count -ne 2) {
            throw "Invalid field definition '$field'. Expected name:type."
        }

        $name = $parts[0].Trim()
        $type = $parts[1].Trim()
        if ([string]::IsNullOrWhiteSpace($name) -or [string]::IsNullOrWhiteSpace($type)) {
            throw "Invalid field definition '$field'."
        }

        $fields += [pscustomobject]@{
            Name = $name
            Type = $type
        }
    }

    return $fields
}

function Join-RecordComponents {
    param([object[]]$Fields)
    return ($Fields | ForEach-Object { "$($_.Type) $($_.Name)" }) -join ', '
}

function Join-AssemblerArguments {
    param([object[]]$Mandatory, [object[]]$Optional)

    $args = @()
    foreach ($field in $Mandatory) {
        $args += "mandatoryOptions.$($field.Name)()"
    }
    foreach ($field in $Optional) {
        $args += "optionalOptions.$($field.Name)()"
    }
    return $args -join ', '
}

function New-OptionMetadataLines {
    param(
        [string]$Kind,
        [object[]]$Fields
    )

    $lines = @()
    foreach ($field in $Fields) {
        $descriptionWords = Convert-CamelToWords $field.Name
        $description = "$([char]::ToUpperInvariant($descriptionWords[0]))$($descriptionWords.Substring(1))."
        $lines += "                .$Kind(`"$($field.Name)`", $($field.Type).class, option -> option"
        $lines += "                        .cliName(`"--$(Convert-CamelToKebabCase $field.Name)`")"
        $lines += "                        .restName(`"$($field.Name)`")"
        if ($Kind -eq "optionalOption" -and $field.Type -eq "boolean") {
            $lines += "                        .description(`"$description`")"
            $lines += "                        .documentedDefaultValue(`"false`"))"
        }
        else {
            $lines += "                        .description(`"$description`"))"
        }
    }
    return $lines
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $repoRoot ("src/main/java/de/gupta/automation/task/implementations/" + $ImplementationPath)
$domainDir = Join-Path $sourceRoot "domain"
$frameworkDir = Join-Path $sourceRoot "framework"

$domainPackage = "de.gupta.automation.task.implementations." + ($ImplementationPath -replace '[\\/]', '.') + ".domain"
$frameworkPackage = "de.gupta.automation.task.implementations." + ($ImplementationPath -replace '[\\/]', '.') + ".framework"

$mandatoryFieldList = Parse-Fields $MandatoryFields
$optionalFieldList = Parse-Fields $OptionalFields

if ($mandatoryFieldList.Count -eq 0) {
    throw "At least one mandatory field is required."
}

$baseName = Convert-ToPascalCase $TaskName
$mandatoryClass = "${baseName}MandatoryOptions"
$optionalClass = "${baseName}OptionalOptions"
$inputClass = "${baseName}Input"
$taskClass = "${baseName}Task"
$assemblerClass = "${baseName}InputAssembler"
$validatorClass = "${baseName}Validator"
$registrationClass = "${baseName}Registration"

$files = @(
    (Join-Path $domainDir "$mandatoryClass.java"),
    (Join-Path $domainDir "$optionalClass.java"),
    (Join-Path $domainDir "$inputClass.java"),
    (Join-Path $domainDir "$taskClass.java"),
    (Join-Path $domainDir "$assemblerClass.java"),
    (Join-Path $domainDir "$validatorClass.java"),
    (Join-Path $frameworkDir "$registrationClass.java")
)

foreach ($file in $files) {
    if (Test-Path $file) {
        throw "Refusing to overwrite existing file: $file"
    }
}

$null = New-Item -ItemType Directory -Force -Path $domainDir
$null = New-Item -ItemType Directory -Force -Path $frameworkDir

$mandatoryComponents = Join-RecordComponents $mandatoryFieldList
$optionalComponents = Join-RecordComponents $optionalFieldList
$inputComponents = Join-RecordComponents ($mandatoryFieldList + $optionalFieldList)
$assemblerArguments = Join-AssemblerArguments $mandatoryFieldList $optionalFieldList

Set-Content -LiteralPath (Join-Path $domainDir "$mandatoryClass.java") -Value @"
package $domainPackage;

public record $mandatoryClass($mandatoryComponents)
{
}
"@

Set-Content -LiteralPath (Join-Path $domainDir "$optionalClass.java") -Value @"
package $domainPackage;

public record $optionalClass($optionalComponents)
{
}
"@

Set-Content -LiteralPath (Join-Path $domainDir "$inputClass.java") -Value @"
package $domainPackage;

public record $inputClass($inputComponents)
{
}
"@

Set-Content -LiteralPath (Join-Path $domainDir "$taskClass.java") -Value @"
package $domainPackage;

import de.gupta.automation.task.framework.domain.port.TaskFunction;

public final class $taskClass implements TaskFunction<$inputClass, $OutputType>
{
	@Override
	public $OutputType execute(final $inputClass input)
	{
		throw new UnsupportedOperationException("Implement task logic.");
	}
}
"@

Set-Content -LiteralPath (Join-Path $domainDir "$assemblerClass.java") -Value @"
package $domainPackage;

import de.gupta.automation.task.framework.domain.port.TaskInputAssembler;

public final class $assemblerClass
		implements TaskInputAssembler<$inputClass, $mandatoryClass, $optionalClass>
{
	@Override
	public $inputClass assemble(final $mandatoryClass mandatoryOptions,
	                             final $optionalClass optionalOptions)
	{
		return new $inputClass($assemblerArguments);
	}
}
"@

Set-Content -LiteralPath (Join-Path $domainDir "$validatorClass.java") -Value @"
package $domainPackage;

import de.gupta.automation.task.framework.domain.port.TaskValidator;

public final class $validatorClass implements TaskValidator<$mandatoryClass, $optionalClass>
{
	@Override
	public void validateMandatory(final $mandatoryClass mandatoryOptions)
	{
		// TODO: implement mandatory option validation
	}

	@Override
	public void validateOptional(final $optionalClass optionalOptions)
	{
		// TODO: implement optional option validation
	}
}
"@

$descriptorBeanName = ($TaskName -replace '-', '') + "Descriptor"
$registrationLines = @(
    "package $frameworkPackage;",
    "",
    "import de.gupta.automation.task.framework.domain.port.TaskDescriptor;",
    "import de.gupta.automation.task.framework.registry.TaskDescriptorBuilder;",
    "import $domainPackage.$assemblerClass;",
    "import $domainPackage.$inputClass;",
    "import $domainPackage.$mandatoryClass;",
    "import $domainPackage.$optionalClass;",
    "import $domainPackage.$taskClass;",
    "import $domainPackage.$validatorClass;",
    "import org.springframework.context.annotation.Bean;",
    "import org.springframework.context.annotation.Configuration;",
    "import org.springframework.http.MediaType;",
    "",
    "@Configuration(proxyBeanMethods = false)",
    "public class $registrationClass",
    "{",
    "	@Bean",
    "	TaskDescriptor<$OutputType, $mandatoryClass, $optionalClass> $descriptorBeanName()",
    "	{",
    "		return TaskDescriptorBuilder.task(`"$TaskName`")",
    "				.version(`"1`")",
    "				.types($inputClass.class, $OutputType.class, $mandatoryClass.class, $optionalClass.class)",
    "				.function(new $taskClass())",
    "				.assembler(new $assemblerClass())",
    "				.validator(new $validatorClass())"
)

$registrationLines += New-OptionMetadataLines "mandatoryOption" $mandatoryFieldList
$registrationLines += New-OptionMetadataLines "optionalOption" $optionalFieldList
$registrationLines += @(
    "                .cli(cli -> cli",
    "                        .commandName(`"$TaskName`")",
    "                        .description(`"TODO: describe $TaskName.`")"
)

if ($OutputType -eq "String") {
    $registrationLines += @(
        "                        .outputRenderer(output -> output))",
        "                .rest(rest -> rest",
        "                        .path(`"/api/tasks/$TaskName/execute`")",
        "                        .description(`"TODO: describe $TaskName.`")",
        "                        .produces(MediaType.TEXT_PLAIN_VALUE)",
        "                        .outputRenderer(output -> output))"
    )
}
else {
    $registrationLines += @(
        "                        .outputRenderer(output -> String.valueOf(output)))",
        "                .rest(rest -> rest",
        "                        .path(`"/api/tasks/$TaskName/execute`")",
        "                        .description(`"TODO: describe $TaskName.`")",
        "                        .produces(MediaType.APPLICATION_JSON_VALUE)",
        "                        .outputRenderer(output -> output))"
    )
}

$registrationLines += @(
    "                .build();",
    "	}",
    "}"
)

Set-Content -LiteralPath (Join-Path $frameworkDir "$registrationClass.java") -Value ($registrationLines -join [Environment]::NewLine)

Write-Host "Scaffolded task: $TaskName"
Write-Host "Created files:"
foreach ($file in $files) {
    Write-Host " - $file"
}

if ($OutputType -ne "String") {
    Write-Host "Note: $OutputType was referenced but not generated. Create that output type if it does not already exist."
}
