package com.TUIChallengeApp.TUIChallenge;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class AllItems {
	AndroidDriver driver;

	public AllItems(AndroidDriver driver) {
		this.driver = driver;
		AppiumPageFactory.initElements(new AppiumFieldDecorator(driver), this);

	}

	@AndroidFindBy(xpath = "//android.widget.TextView[@text=\"All\"]")
	private WebElement allItems;

	@AndroidFindBy(xpath = "//android.widget.ImageView[contains(@id,'content_card_hotel_name']")
	private List<WebElement> iconsSelection;

	@AndroidFindBy(xpath = "//android.view.View[contains(@id,'content_card_price_button']")
	private List<WebElement> hotelPrices;
	
	@AndroidFindBy(xpath="//android.view.View[@content-desc=\"Hotels\"]")
	private WebElement hotelsTab;

	public void allItemsCheck() {
		//Checking for product catalog is showing or not.
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.attributeContains(allItems, "text", "All"));
	}

	public void selectItems() {
		int itemCount = iconsSelection.size();
		for (int i = 0; i < itemCount; i++) {
			String hotelName = iconsSelection.get(i).getText();
//			if(hotelName.equals("Hotel: H10 London Waterloo")) {
//				String hotelPrice=hotelPrices.get(i).getText();
//				System.out.println(hotelPrice);
//			}
			System.out.println("HotelNameIs"+ hotelName);

		}

	}
	
	public void hotelTabValidation() {
		//Checking for product catalog is showing or not.
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		wait.until(ExpectedConditions.attributeContains(hotelsTab, "text", "Hotels"));
	}

	public Boolean hotelsTab() {
		hotelsTab.click();
		Boolean hotelTabStatus=hotelsTab.isDisplayed();
		return hotelTabStatus;
	}

}
