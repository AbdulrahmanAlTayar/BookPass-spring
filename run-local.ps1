<#
.SYNOPSIS
    Runs the BookPass backend locally with environment variables.

.DESCRIPTION
    This script loads secrets from 'local-secrets.env' (if it exists)
    and runs the Spring Boot application using Maven Wrapper.
    
    It serves as a convenience wrapper around 'mvnw spring-boot:run'.

.EXAMPLE
    .\run-local.ps1
#>

$ErrorActionPreference = "Stop"

$SecretsFile = "$PSScriptRoot\local-secrets.env"

if (Test-Path $SecretsFile) {
    Write-Host "Loading secrets from $SecretsFile..." -ForegroundColor Cyan
    Get-Content $SecretsFile | Where-Object { $_ -match '=' -and -not ($_ -match '^#') } | ForEach-Object {
        $key, $value = $_.Split('=', 2)
        [System.Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim(), [System.EnvironmentVariableTarget]::Process)
    }
} else {
    Write-Warning "No 'local-secrets.env' file found. Application may fail if secrets are required."
    Write-Warning "Copy 'local-secrets.env.template' to 'local-secrets.env' and configure it."
}

Write-Host "Starting Spring Boot Application..." -ForegroundColor Green
& "$PSScriptRoot\mvnw.cmd" spring-boot:run
