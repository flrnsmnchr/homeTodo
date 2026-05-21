$ErrorActionPreference = "Stop"

$backendRoot = Split-Path -Parent $PSScriptRoot
$playwrightDir = Join-Path $backendRoot "build\\playwright"
$dbPath = Join-Path $playwrightDir "hometodo-e2e.db"
$logPath = Join-Path $playwrightDir "hometodo-e2e.log"

New-Item -ItemType Directory -Force -Path $playwrightDir | Out-Null

if (Test-Path -LiteralPath $dbPath) {
    Remove-Item -LiteralPath $dbPath -Force
}

$env:SPRING_DATASOURCE_URL = "jdbc:sqlite:$($dbPath -replace '\\', '/')"
$env:LOGGING_FILE_NAME = $logPath

cd $backendRoot
& (Join-Path $backendRoot "gradlew.bat") bootRun
