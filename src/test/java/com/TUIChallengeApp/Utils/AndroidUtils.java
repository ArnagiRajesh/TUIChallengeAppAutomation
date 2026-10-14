package com.TUIChallengeApp.Utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class AndroidUtils {

	AndroidDriver driver;

	public AndroidUtils(AndroidDriver driver) {
		this.driver = driver;
	}

	public void scrollGesture(String HotelName) {
		driver.findElement(AppiumBy
				.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + HotelName + "\"))"));
	}
}
