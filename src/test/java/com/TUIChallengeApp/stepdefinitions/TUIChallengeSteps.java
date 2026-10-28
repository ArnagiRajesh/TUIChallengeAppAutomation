package com.TUIChallengeApp.stepdefinitions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.TUIChallengeApp.hooks.CucumberHooks;
import com.TUIChallengeApp.Utils.JsonTestData;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TUIChallengeSteps {
	private static final Logger LOG = LoggerFactory.getLogger(TUIChallengeSteps.class);
	private final CucumberHooks hooks;

	public TUIChallengeSteps(CucumberHooks hooks) {
		this.hooks = hooks;
	}

	@When("I log in with test data {string}")
	public void logInWithTestData(String dataset) {
		hooks.login.enterUsername(JsonTestData.value(dataset, "username"));
		hooks.login.enterPassword(JsonTestData.value(dataset, "password"));
		hooks.login.selectDateOfBirth(JsonTestData.value(dataset, "date"));
		hooks.login.submitLogin();
		LOG.atInfo().addKeyValue("event", "login_submitted")
				.addKeyValue("dataset", dataset).log("Login submitted using external test data");
	}

	@When("I submit login with test data {string} without a password")
	public void submitLoginWithoutPassword(String dataset) {
		hooks.login.enterUsername(JsonTestData.value(dataset, "username"));
		hooks.login.selectDateOfBirth(JsonTestData.value(dataset, "date"));
		hooks.login.submitLogin();
	}

	@Then("the all items catalog is displayed")
	public void allItemsCatalogIsDisplayed() {
		hooks.items.assertCatalogDisplayed();
	}

	
	@When("I open the Hotels tab")
	public void openHotelsTab() {
		hooks.items.openHotelsTabAndAssertDisplayed();
	}

	@Then("the hotels tab is displayed")
	public void hotelsTabIsDisplayed() {
		hooks.items.assertHotelsTabDisplayed();
	}

	@Then("the password validation message is {string}")
	public void passwordValidationMessageIs(String expectedMessage) {
		hooks.login.assertPasswordError(expectedMessage);
	}

	@When("I open the listing from test data {string}")
	public void openListing(String dataset) {
		String listing = JsonTestData.value(dataset, "tab");
		switch (listing) {
			case "All" -> hooks.items.assertCatalogDisplayed();
			case "Hotels" -> hooks.items.openHotelsTabAndAssertDisplayed();
			default -> throw new IllegalArgumentException("Unsupported hotel listing: " + listing);
		}
	}

	@Then("the hotel from test data {string} is displayed")
	public void hotelFromTestDataIsDisplayed(String dataset) {
		hooks.items.scrollToHotelAndAssertVisible(JsonTestData.value(dataset, "hotel"));
	}

	@When("I open the Holidays tab")
	public void openHolidaysTab() {
		hooks.holidays.openHolidaysTab();
	}

	@Then("the first holiday is displayed")
	public void firstHolidayIsDisplayed() {
		hooks.holidays.assertFirstHolidayDisplayed();
		
		
		
	}
}