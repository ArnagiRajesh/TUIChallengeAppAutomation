package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class AllItems {
	private final AndroidDriver driver;
	private final WebDriverWait wait;

	public AllItems(AndroidDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='All']")
	private WebElement allItems;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Hotels']")
	private WebElement hotelsTab;

	public void assertCatalogDisplayed() {
		wait.until(ExpectedConditions.visibilityOf(allItems));
		if (!allItems.isDisplayed()) {
			throw new AssertionError("The All items catalog is not displayed");
		}
	}

	public void assertHotelsTabDisplayed() {
		wait.until(ExpectedConditions.visibilityOf(hotelsTab));
		if (!hotelsTab.isDisplayed()) {
			throw new AssertionError("The Hotels tab is not displayed");
		}
	}

	public void openHotelsTabAndAssertDisplayed() {
		wait.until(ExpectedConditions.elementToBeClickable(hotelsTab)).click();
		assertHotelsTabDisplayed();
	}

	public void scrollToHotelAndAssertVisible(String hotelName) {
		By hotelLocator = textLocator(hotelName);
		try {
			driver.findElement(AppiumBy.androidUIAutomator(
					"new UiScrollable(new UiSelector()).scrollIntoView(text(\""
							+ escapeUiAutomatorText(hotelName) + "\"))"));
		} catch (NoSuchElementException exception) {
			throw new AssertionError("Could not find hotel '" + hotelName
					+ "' after scrolling. Current screen source: " + driver.getPageSource(), exception);
		}
		WebElement hotel = wait.until(ExpectedConditions.visibilityOfElementLocated(hotelLocator));
		if (!hotel.isDisplayed()) {
			throw new AssertionError("Hotel was not displayed after scrolling: " + hotelName);
		}
	}

	private By textLocator(String text) {
		return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + escapeUiAutomatorText(text) + "\")");
	}

	private String escapeUiAutomatorText(String text) {
		return text.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}