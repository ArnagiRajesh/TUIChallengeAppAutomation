package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;

import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class HolodaysTab {
	AndroidDriver driver;

	public HolodaysTab(AndroidDriver driver) {
		this.driver = driver;
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);

	}
	
	@AndroidFindBy(xpath="//android.view.View[@content-desc=\"Holidays\"]")
	private WebElement holidaysTab;
	
	@AndroidFindBy(xpath="//android.widget.TextView[@text=\"Riu Madeira\"]")
	private WebElement firstElement;
	
	public Boolean firstElementOnHotelsTab() {
		Boolean hotelsTabFirstElement=firstElement.isDisplayed();
		return hotelsTabFirstElement;
	}
	
	public Boolean hotelsTab() throws InterruptedException {
		holidaysTab.click();
		Boolean hotelTabStatus=holidaysTab.isEnabled();
		return hotelTabStatus;
	}

}
