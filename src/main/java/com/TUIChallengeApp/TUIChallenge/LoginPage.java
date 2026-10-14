package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;

import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class LoginPage {

	AndroidDriver driver;

	public LoginPage(AndroidDriver driver) {
		this.driver = driver;
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);

	}

	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id=\"username_input_field\"]")
	private WebElement userName;

	@AndroidFindBy(xpath = "//android.widget.EditText[@resource-id=\"password_input_field\"]")
	private WebElement password;

	@AndroidFindBy(xpath = "//android.view.View[@content-desc='Calendar']")
	private WebElement calenderIcon;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id=\"date_of_birth_dialog\"]/android.view.View/android.view.View[1]/android.view.View[1]/android.widget.Button")
	private WebElement editIconOnCalender;

	@AndroidFindBy(xpath = "//android.widget.EditText")
	private WebElement enterDateTab;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id=\"date_of_birth_dialog_confirm_button\"]/android.widget.Button")
	private WebElement confirmDate;

	@AndroidFindBy(xpath = "//android.view.View[@resource-id=\"login_form_submit_button\"]")
	private WebElement loginSubmit;
	
	@AndroidFindBy(xpath= "//android.widget.TextView[@resource-id=\"password_input_field_error\"]")
	private WebElement passwordErrorValidaiton;
	

	public void enterUserName(String UserName) throws InterruptedException {
		userName.click();
		userName.click();
		userName.sendKeys(UserName);
	}

	public void enterPassword(String Password) {
		password.click();
		password.sendKeys(Password);
	}

	public void clickOnCalenderIcon() {
		calenderIcon.click();
	}

	public void editCalenderButton() {
		editIconOnCalender.click();
	}

	public void enterDateTab(String Date) {
		enterDateTab.click();
		enterDateTab.click();
		enterDateTab.sendKeys(String.valueOf(Date));

	}

	public void confirmDate() {
		confirmDate.click();
	}

	public void submitButton() throws InterruptedException {
		loginSubmit.click();
	}
	public String passwordErrorValidation() {
		String errorMessage=passwordErrorValidaiton.getText();
		return errorMessage;
	}
	

}
