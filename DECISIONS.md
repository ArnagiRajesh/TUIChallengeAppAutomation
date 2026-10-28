# Automation Framework Decisions

> **Latest emulator result:** The current Cucumber suite passed on the configured Android emulator. The latest verified run records 6 executed tests, 0 failures, 0 errors, and 0 skipped.

## 1. Why I Chose This Project Structure

This project automates an Android application with Appium and expresses user flows in Cucumber. I kept the existing Maven/JUnit Platform structure and organized responsibilities by the roles already present in the repository, rather than introducing extra framework layers without a demonstrated need.

### Current package and resource structure

- `src/main/java/.../TUIChallenge` — Android page objects (`LoginPage`, `AllItems`, `HolidaysPage`) and the Appium page-field initializer. Page objects own selectors, actions, waits, and checks of page state.
- `src/test/java/.../hooks` — Cucumber lifecycle hooks; creates the scenario's driver and page objects, then tears them down.
- `src/test/java/.../stepdefinitions` — Cucumber glue that coordinates business actions and delegates UI checks to page objects.
- `src/test/java/.../Utils` — Driver/session setup and external JSON test-data loading.
- `src/test/java/.../runner` — JUnit Platform Cucumber suite, configured to create HTML, JSON, and failed-location rerun output.
- `src/test/resources/features` — Gherkin scenarios and tags.
- `src/test/resources/testdata` — Named scenario inputs, external to the feature file.
- `src/test/resources/data.properties` — Non-secret default execution settings. Environment variables and Java system properties can override them.
- `config` — Example cloud capability JSON; provider-specific actual values belong in a private, untracked file.

### Design choices

- **Page Object Model:** Keeps Android locators and expected UI states with the page that owns them.
- **Scenario-scoped driver:** `BaseTest` uses `ThreadLocal<AndroidDriver>` and removes the driver at teardown. This prevents accidental cross-thread driver sharing; it does not allocate devices or by itself enable parallel execution.
- **External test data:** Feature scenarios identify named JSON datasets, avoiding embedded credentials and repeated example values in Gherkin.
- **Cucumber tags:** `@smoke` denotes a small critical-flow subset, while `@regression` denotes the full current set. Tag expressions allow targeted execution.
- **Rerun behavior:** Cucumber records failed locations in `target/rerun.txt`; `scripts/test-with-rerun.ps1` performs one second pass and preserves first-pass Cucumber outputs. This is a bounded rerun, not a retry-until-green policy.
- **Target configuration:** Local Android (emulator or ADB-visible physical device) uses a local Appium server; remote Android execution uses an Appium endpoint plus a JSON file of provider capabilities. Credentials are supplied externally and are not stored in repository files.

## 2. AI Usage and Corrections

AI assistance was used during project review, implementation, and documentation. No AI service is used at test runtime, and the framework does not send test data to an AI service.

### AI Suggestion - Project Structure

- Suggested a generic Page Object Model approach for separating UI interactions from Cucumber steps.

### Correction Made

- Kept the project's Maven, Appium, and Cucumber architecture Android-specific rather than introducing browser automation components.
- Retained the existing page, hook, step-definition, runner, and utility locations.
- Put selectors, page actions, waits, and UI assertions in `LoginPage`, `AllItems`, and `HolidaysPage`; kept step definitions as scenario orchestration.
- Added thread-local Android driver ownership and teardown cleanup without claiming that parallel worker/device allocation is already configured.

### AI Suggestion - Device and Cloud Configuration

- Suggested making the Appium endpoint and device capabilities configurable for local or remote Android execution.

### Correction Made

- Added configuration precedence for Java system properties, environment variables, and `data.properties` defaults.
- Added a generic remote Appium endpoint and external JSON capabilities file, plus an example capabilities template.
- Did not add BrowserStack-specific driver classes or hardcoded provider credentials; actual authentication and capability names must follow the selected cloud provider's documentation.
- Kept local emulator and physical-device selection based on ADB UDID and avoided describing unexecuted real-device/cloud runs as verified.

### AI Suggestion - Feature Files and Scenario Selection

- Suggested organizing user flows as Cucumber scenarios and using tags for selective execution.

### Correction Made

- Used the actual application flows: successful login/catalog, missing-password validation, hotel visibility in two listings, and Holidays tab content.
- Consolidated the overlapping All/Hotels scrolling checks into one Scenario Outline with distinct JSON datasets.
- Required the hotel target to be visible after scrolling; used `UiScrollable(new UiSelector()).scrollIntoView(text(...))` as exercised by the Android run.
- Retained `@smoke` and `@regression` tags and moved scenario inputs out of Gherkin Examples into JSON.
- The current suite passed on the emulator (Surefire: 6 executed tests, 0 failures, 0 errors, 0 skipped).

### AI Suggestion - Retry and Reporting

- Suggested recording failed Cucumber scenario locations for reruns and retaining standard reports.

### Correction Made

- Configured Cucumber's rerun formatter to write `target/rerun.txt` and added a bounded second-pass PowerShell script.
- Preserved first-attempt reports so a successful retry cannot hide an initial failure; no unlimited retry loop is used.

### AI Suggestion - Documentation

- Suggested updating the README and this decisions record to describe the resulting framework and execution approach.

### Correction Made

- Updated documentation to use the actual project paths, current local/remote configuration, tag filters, rerun commands, and report locations.
- Recorded the real emulator execution evidence and kept unverified cloud-provider behavior explicitly labeled as configuration guidance rather than a completed test.
- Documented future work and the limitations of Android-only locators and device provisioning.


## 3. What I Would Add Given More Time

- Correct and validate the failing `President` hotel example against the actual app's visible hotel data; preferably select a confirmed hotel name from a captured UI hierarchy.
- Add automated tests for configuration parsing, capability JSON, and environment/system-property precedence without requiring a device.
- Add failure screenshots and attach them to Cucumber reports.
- Add a provider-specific cloud execution profile and a provider smoke test once the target service is selected.
- Add CI workflow(s) that provision a device/cloud session, securely inject credentials, run tests, and retain reports.
- Add a device pool and explicit worker-to-device mapping before enabling parallel scenarios.
- Consider iOS only if it becomes an explicit requirement; the current pages, driver, locators, and app are Android-specific.

## 4. CI and Parallel Execution Strategy

### Current implementation

- Android scenarios can use a local emulator or an ADB-authorized physical device with a local Appium server.
- Android scenarios can be configured for a remote Android device when a compatible Appium cloud endpoint, uploaded app ID, and provider-specific capabilities are supplied.
- The driver is held per thread and released after a scenario.
- Cucumber parallel execution is **not enabled** by default.

### Proposed CI workflow

1. Check out the project and provision JDK 17, Maven, Appium/UiAutomator2 (local execution), and an Android device or cloud session.
2. Supply the APK or provider app ID and device settings. Inject cloud credentials through the CI secret store; do not commit them or include them in logs.
3. Run `mvn clean test` or the bounded `scripts/test-with-rerun.ps1` workflow.
4. Publish Surefire XML plus `target/cucumber.html`, `target/cucumber.json`, and `target/rerun.txt` as build artifacts.
5. Mark the build successful only if the final test invocation succeeds; retain the first attempt's failure reports even if a rerun passes.

No CI provider, credentials, or cloud account were provided, so a CI workflow and a live cloud run are not claimed as implemented.

### Safe parallel execution

Parallelism requires both independent thread-local drivers **and** one isolated device session per concurrent worker. For CI, provision a device matrix or cloud session per worker, pass a unique UDID/session capability to each worker, enforce provider concurrency limits, and enable Cucumber/JUnit parallel execution only after validating isolation. Do not run concurrent sessions against one emulator or physical device.

## 5. Why These Scenarios Were Selected

The current feature contains five scenario definitions and six executed test cases because the hotel-listing outline has two examples. Selection prioritizes the main login flow, validation, listing navigation/content, and holiday navigation. It does not claim comprehensive feature coverage.

### 1. Successful login opens the All items catalog

**Reason:** Login is the entry point for the browsing flows covered here. Verifying the catalog after submission checks a meaningful post-login state rather than merely checking that the submit action did not throw.

**Tag:** `@smoke`, `@regression`.

### 2. Missing password shows validation

**Reason:** Password is mandatory in the tested login flow. The scenario verifies the user receives the expected `Required` validation message when that field is omitted.

**Tag:** `@regression`.

### 3. Hotels tab is shown after login

**Reason:** Verifies that the user can navigate to the Hotels tab and that the tab itself is visible as expected after login.

**Tag:** `@regression`.

### 4. A target hotel is visible in its listing

**Reason:** The same search/scroll behavior is tested in the All and Hotels listings. A Scenario Outline avoids near-duplicate Gherkin while preserving distinct listing and target-data cases; the assertion checks the expected hotel is visible after scrolling.

**Tag:** `@regression`.

**Result:** The hotel-listing Cucumber Scenario Outline passed for both listing datasets on the emulator. The target hotel is asserted visible after scrolling for both dataset variations.

### 5. Holidays tab displays its first holiday

**Reason:** Exercises a separate navigation tab and validates actual content is visible rather than only checking that the tab can be tapped.

**Tag:** `@smoke`, `@regression`.

## 6. Execution Evidence and Known Limitations

- `mvn -DskipTests package` completed successfully after the configuration changes.
- The focused hotel-listing scenario was run using Cucumber and passed for both outline examples.
- The subsequent `mvn clean test` run reached the Android 16 emulator (`emulator-5554`), started Appium successfully, and passed. The latest Surefire report records **5 tests, 0 failures, 0 errors, 0 skipped**.
- Therefore a clean, passing emulator test run is demonstrated for the current test suite.
- Real-device and remote cloud configuration paths are provided but have not been executed in this environment. Cloud capability names and authentication remain vendor-specific.