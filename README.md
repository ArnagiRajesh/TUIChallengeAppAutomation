# TUI Challenge Android BDD tests

This Maven project uses Java 17, Appium Java Client 11.0.0, Cucumber 8.0.4, and the JUnit Platform runner. The original page objects and their locator definitions are retained. Test flows are expressed as Gherkin scenarios in `src/test/resources/features/TUIChallenge.feature`.



## Prerequisites

- JDK 17 
- Maven
- Node.js with the Appium server installed
- The Appium UiAutomator2 driver installed (`appium driver install uiautomator2`)
- An Android emulator/device available through ADB
- `src/test/resources/TUIChallengeApp.apk`


The device name, UDID, and Appium host/port are set in `src/test/resources/data.properties`. The Appium Java service builder discovers a Windows global Appium installation under the current user's npm directory. For a custom Appium server location, set `APPIUM_JS_PATH`.

## Design Patterns
Page Object Model (POM)
Page Factory Pattern
Cucumber BDD Architecture
Externalized Test Data using JSON
Reusable Utility Classes

##Verify Installation

#Verify Java 
java -version

#Verify Maven
mvn -version

#Verify Node
node -v

#Verify Connected Android Device
Ensure an Android Emulator or Real Android Device is connected before running the tests.

adb devices

#Verify Appium
appium -v

#Before Running the Framework Ensure that:

Appium Server is running.
An Android Emulator or Real Android Device is connected.
The required execution mode and device details are configured in src/test/resources/config/config.properties.
The required test data is available in the JSON files under src/test/resources/testdata.


## Run

Run the Cucumber scenarios with `mvn clean test`. The Cucumber HTML report is generated at `target/cucumber.html`; the JSON report is generated at `target/cucumber.json`.
