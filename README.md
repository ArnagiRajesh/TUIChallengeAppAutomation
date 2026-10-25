# TUI Challenge Android Automation

## Overview

This project is an Android mobile automation framework for the TUI Challenge app, built with:

- Java 17 and Maven
- Appium Java Client 11 and Selenium support 4.51
- Cucumber 8 with the JUnit Platform runner and PicoContainer
- Page Object Model (POM)
- SLF4J with Logback
- Jackson JSON support for externalized scenario data and capabilities

The current suite covers login, password validation, hotel listing/scroll behavior, and the Holidays tab. It runs as Cucumber scenarios and has been verified on an Android emulator. Configuration is provided for local Android emulators, ADB-connected physical Android devices, and compatible remote Android Appium services; the real-device and cloud paths have not been exercised in this workspace.

---

# Framework Design

## Design Patterns and Responsibilities

- **Page Object Model:** Page classes own Android locators, UI actions, waits, and page-state assertions.
- **Thread-local driver ownership:** Each scenario's `AndroidDriver` is held in `ThreadLocal` and released during teardown. Parallel execution is not enabled by default and also requires a separate device/session per worker.
- **Cucumber BDD:** Feature files express user scenarios; step definitions orchestrate page-object actions.
- **Externalized JSON data:** Named datasets are stored separately from the Gherkin feature.
- **Reusable support:** Driver setup, JSON test-data access, and Cucumber lifecycle hooks are kept in dedicated classes.

## Project Structure

- `src/main/java/com/TUIChallengeApp/TUIChallenge` — `LoginPage`, `AllItems`, `HolidaysPage`, and `AppiumPageFactory`.
- `src/test/java/com/TUIChallengeApp/hooks` — Scenario driver/page setup and teardown.
- `src/test/java/com/TUIChallengeApp/stepdefinitions` — Cucumber step definitions.
- `src/test/java/com/TUIChallengeApp/runner` — JUnit Platform Cucumber runner.
- `src/test/java/com/TUIChallengeApp/Utils` — Driver management and JSON test-data reader.
- `src/test/resources/features` — Cucumber feature files and scenario tags.
- `src/test/resources/testdata/scenarios.json` — Named test data.
- `src/test/resources/data.properties` — Default Appium and device configuration.
- `src/test/resources/logback-test.xml` — Test logging configuration.
- `config/cloud-capabilities.example.json` — Placeholder template for provider-specific capabilities; do not put secrets in this example.

---

# Prerequisites

Install or configure:

- JDK 17
- Maven
- Android SDK Platform Tools (`adb`) for local Android targets
- Node.js and Appium 2 with the UiAutomator2 driver for a locally managed Appium server
- The app at `src/test/resources/TUIChallengeApp.apk` for local execution; for cloud runs, upload the app to the provider and use its app ID

## Verify the tools

```powershell
java -version
mvn -version
node -v
appium -v
adb devices -l
```

For a local Appium installation, install UiAutomator2 if needed:

```powershell
appium driver install uiautomator2
```

The device should appear in `adb devices -l` with state `device`. Authorize USB debugging for physical Android devices.

---

# Framework Configuration

Default non-secret settings are in `src/test/resources/data.properties`. Configuration values can be overridden by Java system properties or environment variables. Precedence is: Java system property, environment variable, then `data.properties`.

Useful properties and environment-variable equivalents:

| Property | Environment variable | Purpose |
|---|---|---|
| `executionMode` | `EXECUTION_MODE` | `local` (default) or `remote` |
| `ipAddress` | `IP_ADDRESS` | Local Appium bind address |
| `port` | `PORT` | Local Appium port |
| `appiumServerUrl` | `APPIUM_SERVER_URL` | Remote Appium endpoint; also usable locally if managing Appium separately |
| `AndroidDeviceName` | `ANDROID_DEVICE_NAME` | Appium device name |
| `udid` | `UDID` | ADB serial for a local emulator or physical device |
| `app` | `APP` | Local APK path or provider app reference |
| `capabilitiesFile` | `CAPABILITIES_FILE` | JSON file containing additional Appium/provider capabilities |
| `appiumStartupTimeoutSeconds` | `APPIUM_STARTUP_TIMEOUT_SECONDS` | Local Appium readiness timeout; default is 90 seconds |
| `appiumJsPath` | `APPIUM_JS_PATH` | Appium JavaScript entry point for local service startup |

No cloud credentials are committed. Provide them through your cloud provider's recommended secret mechanism and provider-specific capabilities.

## Local Emulator Execution

Start an Android emulator and verify its serial using `adb devices -l`. Set `UDID` to that exact serial when needed. Local mode starts Appium from the configured local installation and uses the APK at the default project path unless `APP` overrides it.

```powershell
$env:UDID = 'emulator-5554'
mvn clean test
```

If Appium is not auto-discovered, set `APPIUM_JS_PATH` to the Appium JavaScript entry point.

## Physical Android Device Execution

Enable USB debugging, authorize the computer, connect the device, and confirm it is listed as `device` rather than `unauthorized`:

```powershell
adb devices -l
$env:UDID = '<serial-shown-by-adb>'
mvn clean test
```

Use the same test suite and an APK compatible with the device. Physical-device execution support is configured but has not been separately validated in this workspace.

## Remote Android / Cloud Execution

For a compatible Appium cloud provider, upload the APK and obtain its app reference. Copy `config/cloud-capabilities.example.json` to a private JSON file, replace the placeholders with the provider's required device, platform, app, and namespaced options, then configure the remote endpoint:

```powershell
$env:EXECUTION_MODE = 'remote'
$env:APPIUM_SERVER_URL = 'https://<provider-appium-endpoint>/<provider-path>'
$env:CAPABILITIES_FILE = 'config/my-cloud-capabilities.json'
mvn clean test
```

Remote mode skips starting a local Appium service. Endpoint paths, authentication, app-upload identifiers, capability names, and concurrency limits vary by provider; use the provider's documentation. Supply credentials securely, not in source control or logs. This is generic remote Appium support, not a provider-specific integration; cloud execution has not been validated in this workspace.

---

# Test Data

Named input datasets are stored in:

`src/test/resources/testdata/scenarios.json`

Feature files refer to dataset names, and `JsonTestData` loads their values during execution. Update this JSON when changing sample usernames, password values, dates, or hotel names. Do not store production credentials or secrets in the tracked test-data file.

---

# Logging

Test logging uses SLF4J with Logback. Configuration is in `src/test/resources/logback-test.xml`; logs are emitted to the Maven test console. The framework records lifecycle events and avoids logging passwords and cloud credentials. It does not currently write a dedicated `Reports/Execution.log` file.

---

# Running the Tests

Run all Cucumber tests from the project root:

```powershell
mvn clean test
```

Run only smoke or regression scenarios:

```powershell
mvn clean test "-Dcucumber.filter.tags=@smoke"
mvn clean test "-Dcucumber.filter.tags=@regression"
```

The Cucumber runner records failed scenario locations in `target/rerun.txt`. To run the bounded one-time rerun workflow, use PowerShell:

```powershell
./scripts/test-with-rerun.ps1
```

This script performs the initial clean test run and then reruns only recorded failures once. Review both the initial and rerun reports; retry success does not erase the initial failure.

---

# Test Reports

The Cucumber runner generates:

- `target/cucumber.html` — Cucumber HTML report
- `target/cucumber.json` — Cucumber JSON report
- `target/rerun.txt` — Failed scenario locations for rerun
- `target/surefire-reports` — Maven Surefire test results

The latest recorded emulator run completed with 5 tests, 0 failures, 0 errors, and 0 skipped. Re-run the suite in the target environment to produce current evidence.

---

# Future Enhancements

- Add a CI pipeline and publish Cucumber/Surefire reports as build artifacts.
- Add explicit device allocation before enabling parallel execution.
- Validate a selected cloud provider's endpoint, capabilities, app upload, and secret configuration.
- Capture screenshots on failure and attach them to Cucumber reports.
- Add iOS support only if required; the current driver, locators, and application flow are Android-specific.