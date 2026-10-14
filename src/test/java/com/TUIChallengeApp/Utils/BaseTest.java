package com.TUIChallengeApp.Utils;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.io.InputStream;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class BaseTest {

	private static AppiumDriverLocalService service;
	private static Properties properties;

	public static synchronized void startService() throws IOException {
		if (service != null && service.isRunning()) {
			return;
		}

		properties = loadProperties();
		String ipAddress = property("ipAddress", "127.0.0.1");
		int port = Integer.parseInt(property("port", "4723"));
		AppiumServiceBuilder builder = new AppiumServiceBuilder()
				.withIPAddress(ipAddress)
				.usingPort(port);
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
	}

	public AndroidDriver createDriver() throws IOException {
		if (properties == null) {
			properties = loadProperties();
		}

		UiAutomator2Options options = new UiAutomator2Options();
		options.setDeviceName(property("AndroidDeviceName", "emulator-5554"));
		String udid = property("udid", "");
		if (!udid.isBlank()) {
			options.setUdid(udid);
		}
		options.setPlatformName("Android");
		options.setAutomationName("UiAutomator2");
		options.setApp(Path.of(System.getProperty("user.dir"), "src", "test", "resources",
				"TUIChallengeApp.apk").toAbsolutePath().toString());

		String serverUrl = "http://" + property("ipAddress", "127.0.0.1") + ":"
				+ property("port", "4723");
		AndroidDriver driver = new AndroidDriver(URI.create(serverUrl).toURL(), options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		return driver;
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

	private static String property(String key, String defaultValue) {
		return System.getProperty(key, properties.getProperty(key, defaultValue)).trim();
	}

}
