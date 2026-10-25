$ErrorActionPreference = 'Stop'

mvn clean test
$firstExitCode = $LASTEXITCODE
if ($firstExitCode -eq 0) {
    exit 0
}

$rerunFile = Join-Path (Get-Location) 'target/rerun.txt'
if (-not (Test-Path $rerunFile) -or (Get-Item $rerunFile).Length -eq 0) {
    Write-Error "Initial Maven run failed and no failed scenarios were recorded in $rerunFile."
    exit $firstExitCode
}

$attemptOne = Join-Path (Get-Location) 'target/attempt-1'
New-Item -ItemType Directory -Path $attemptOne -Force | Out-Null
foreach ($report in @('cucumber.html', 'cucumber.json', 'rerun.txt')) {
    $source = Join-Path (Get-Location) "target/$report"
    if (Test-Path $source) {
        Copy-Item $source (Join-Path $attemptOne $report) -Force
    }
}

Write-Host 'Retrying only scenarios recorded by Cucumber in target/rerun.txt...'
mvn test '-Dcucumber.features=@target/rerun.txt'
exit $LASTEXITCODE
