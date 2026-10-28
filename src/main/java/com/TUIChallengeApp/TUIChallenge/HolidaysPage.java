package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class HolidaysPage {
	private final WebDriverWait wait;

	public HolidaysPage(AndroidDriver driver) {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Holidays']")
	private WebElement holidaysTab;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Holidays']")
	private WebElement firstHoliday;

	public void openHolidaysTab() {
		wait.until(ExpectedConditions.elementToBeClickable(holidaysTab)).click();
	}

	public void assertFirstHolidayDisplayed() {
		wait.until(ExpectedConditions.visibilityOf(firstHoliday));
		if (!firstHoliday.isDisplayed()) {
			throw new AssertionError("The first holiday text is not displayed on the Holidays page");
		}
	}
}