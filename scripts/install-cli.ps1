param(
    [Parameter(Mandatory = $true)]
    [string]$CliJarPath,

    [Parameter(Mandatory = $true)]
    [string]$InstallDir
)

$resolvedJarPath = (Resolve-Path $CliJarPath).Path
$null = New-Item -ItemType Directory -Force -Path $InstallDir

$tasksWrapper = @"
@echo off
java -jar "$resolvedJarPath" %*
"@
Set-Content -LiteralPath (Join-Path $InstallDir "tasks.cmd") -Value $tasksWrapper -NoNewline

$commands = & java -jar $resolvedJarPath --list-commands
foreach ($commandName in $commands) {
    if ([string]::IsNullOrWhiteSpace($commandName)) {
        continue
    }

    $wrapper = @"
@echo off
java -jar "$resolvedJarPath" $commandName %*
"@
    Set-Content -LiteralPath (Join-Path $InstallDir ($commandName + ".cmd")) -Value $wrapper -NoNewline
}

Write-Host "Installed CLI wrappers to $InstallDir"
