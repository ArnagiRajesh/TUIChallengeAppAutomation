package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class LoginPage {
	private final WebDriverWait wait;

	public LoginPage(AndroidDriver driver) {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='username_input_field']")
	private WebElement username;

	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='password_input_field']")
	private WebElement password;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Calendar']")
	private WebElement calendarIcon;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id='date_of_birth_dialog']/android.view.View/android.view.View[1]/android.view.View[1]/android.widget.Button")
	private WebElement editCalendarButton;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement dateInput;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id='date_of_birth_dialog_confirm_button']/android.widget.Button")
	private WebElement confirmDateButton;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id='login_form_submit_button']")
	private WebElement submitButton;

	@AndroidFindBy(xpath = "//android.widget.TextView[@resource-id='password_input_field_error']")
	private WebElement passwordError;

	public void enterUsername(String value) {
		username.click();
		username.clear();
		username.sendKeys(value);
	}

	public void enterPassword(String value) {
		password.click();
		password.clear();
		password.sendKeys(value);
	}

	public void selectDateOfBirth(String value) {
		calendarIcon.click();
		editCalendarButton.click();
		dateInput.click();
		dateInput.clear();
		dateInput.sendKeys(value);
		confirmDateButton.click();
	}

	public void submitLogin() {
		wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
	}

	public void assertPasswordError(String expectedMessage) {
		String actualMessage = wait.until(ExpectedConditions.visibilityOf(passwordError)).getText();
		if (!expectedMessage.equals(actualMessage)) {
			throw new AssertionError("Expected password validation '" + expectedMessage
					+ "' but was '" + actualMessage + "'");
		}
	}
}