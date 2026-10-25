package com.TUIChallengeApp.Utils;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class BaseTest {

	private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);
	private static final ObjectMapper JSON = new ObjectMapper();
	private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();
	private static AppiumDriverLocalService service;
	private static volatile Properties properties;

	public static synchronized void startService() throws IOException {
		ensurePropertiesLoaded();
		if (isRemoteExecution()) {
			LOG.atInfo().addKeyValue("event", "local_appium_service_skipped")
					.log("Using configured remote Appium endpoint");
			return;
		}
		if (service != null && service.isRunning()) {
			return;
		}

		String ipAddress = property("ipAddress", "127.0.0.1");
		int port = Integer.parseInt(property("port", "4723"));
		AppiumServiceBuilder builder = new AppiumServiceBuilder()
				.withIPAddress(ipAddress)
				.usingPort(port)
				.withTimeout(Duration.ofSeconds(Long.parseLong(property("appiumStartupTimeoutSeconds", "90"))));
		String appiumJsPath = System.getProperty("appium.js.path",
				System.getenv().getOrDefault("APPIUM_JS_PATH", properties.getProperty("appiumJsPath", ""))).trim();
		if (appiumJsPath.isEmpty()) {
			Path windowsGlobalAppium = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "npm",
					"node_modules", "appium", "build", "lib", "main.js");
			if (Files.isRegularFile(windowsGlobalAppium)) {
				appiumJsPath = windowsGlobalAppium.toString();
			}
		}
		if (!appiumJsPath.isEmpty()) {
			builder.withAppiumJS(Path.of(appiumJsPath).toFile());
		}
		service = builder.build();
		service.start();
		LOG.atInfo().addKeyValue("event", "appium_service_started")
				.addKeyValue("host", ipAddress).addKeyValue("port", port).log("Appium service started");
	}

	public AndroidDriver createDriver() throws IOException {
		ensurePropertiesLoaded();

		UiAutomator2Options options = new UiAutomator2Options();
		options.setDeviceName(property("AndroidDeviceName", "Android Device"));
		String udid = property("udid", "");
		if (!udid.isBlank()) {
			options.setUdid(udid);
		}
		options.setPlatformName("Android");
		options.setAutomationName("UiAutomator2");
		String appPath = property("app", "");
		boolean remoteExecution = isRemoteExecution();
		if (!remoteExecution && appPath.isBlank()) {
			appPath = Path.of(System.getProperty("user.dir"), "src", "test", "resources",
					"TUIChallengeApp.apk").toAbsolutePath().toString();
		}
		if (!appPath.isBlank()) {
			options.setApp(appPath);
		}
		applyAdditionalCapabilities(options, property("capabilitiesFile", ""));

		String serverUrl = property("appiumServerUrl", "");
		if (serverUrl.isBlank()) {
			serverUrl = "http://" + property("ipAddress", "127.0.0.1") + ":"
					+ property("port", "4723");
		}
		AndroidDriver driver = new AndroidDriver(URI.create(serverUrl).toURL(), options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		DRIVER.set(driver);
		LOG.atInfo().addKeyValue("event", "driver_created")
				.addKeyValue("executionMode", property("executionMode", "local"))
				.addKeyValue("device", property("AndroidDeviceName", "Android Device"))
				.addKeyValue("thread", Thread.currentThread().getName()).log("Android driver created");
		return driver;
	}

	public static String getConfiguredValue(String key, String defaultValue) throws IOException {
		ensurePropertiesLoaded();
		return property(key, defaultValue);
	}

	public static AndroidDriver getDriver() {
		AndroidDriver driver = DRIVER.get();
		if (driver == null) {
			throw new IllegalStateException("No AndroidDriver is bound to the current scenario thread");
		}
		return driver;
	}

	public static void quitDriver() {
		AndroidDriver driver = DRIVER.get();
		try {
			if (driver != null) {
				driver.quit();
				LOG.atInfo().addKeyValue("event", "driver_quit")
						.addKeyValue("thread", Thread.currentThread().getName()).log("Android driver quit");
			}
		} catch (RuntimeException exception) {
			LOG.atError().addKeyValue("event", "driver_quit_failed").setCause(exception)
					.log("Failed to quit Android driver");
			throw exception;
		} finally {
			DRIVER.remove();
		}
	}

	public static synchronized void stopService() {
		if (service != null && service.isRunning()) {
			service.stop();
		}
		service = null;
	}

	private static Properties loadProperties() throws IOException {
		Properties loaded = new Properties();
		try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("data.properties")) {
			if (input == null) {
				throw new IOException("Could not find data.properties on the test classpath");
			}
			loaded.load(input);
		}
		return loaded;
	}

	private static synchronized void ensurePropertiesLoaded() throws IOException {
		if (properties == null) {
			properties = loadProperties();
		}
	}

	private static String property(String key, String defaultValue) {
		String environmentValue = System.getenv(environmentKey(key));
		String configuredValue = properties.getProperty(key, defaultValue);
		return System.getProperty(key, environmentValue == null ? configuredValue : environmentValue).trim();
	}

	private static String environmentKey(String propertyName) {
		return propertyName.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase();
	}

	private static boolean isRemoteExecution() {
		return "remote".equalsIgnoreCase(property("executionMode", "local"));
	}

	private static void applyAdditionalCapabilities(UiAutomator2Options options, String capabilitiesFile)
			throws IOException {
		if (capabilitiesFile.isBlank()) {
			return;
		}
		Path path = Path.of(capabilitiesFile);
		if (!path.isAbsolute()) {
			path = Path.of(System.getProperty("user.dir")).resolve(path).normalize();
		}
		if (!Files.isRegularFile(path)) {
			throw new IOException("Capabilities JSON file does not exist: " + path);
		}
		try (InputStream input = Files.newInputStream(path)) {
			Map<String, Object> capabilities = JSON.readValue(input, new TypeReference<Map<String, Object>>() { });
			if (capabilities == null) {
				throw new IOException("Capabilities JSON must contain a top-level object: " + path);
			}
			for (Map.Entry<String, Object> capability : capabilities.entrySet()) {
				options.setCapability(capability.getKey(), capability.getValue());
			}
		}
	}

}
