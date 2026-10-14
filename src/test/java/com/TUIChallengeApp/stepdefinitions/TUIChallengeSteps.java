package com.TUIChallengeApp.stepdefinitions;

import com.TUIChallengeApp.hooks.CucumberHooks;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TUIChallengeSteps {

    private final CucumberHooks hooks;

    public TUIChallengeSteps(CucumberHooks hooks) {
        this.hooks = hooks;
    }

    @When("I log in with username {string}, password {string}, and date {string}")
    public void logInWithCredentials(String username, String password, String date) throws InterruptedException {
        hooks.login.enterUserName(username);
        hooks.login.enterPassword(password);
        hooks.login.clickOnCalenderIcon();
        hooks.login.editCalenderButton();
        hooks.login.enterDateTab(date);
        hooks.login.confirmDate();
        hooks.login.submitButton();
    }

    @When("I submit login with username {string} and date {string} without a password")
    public void submitLoginWithoutPassword(String username, String date) throws InterruptedException {
        hooks.login.enterUserName(username);
        hooks.login.clickOnCalenderIcon();
        hooks.login.editCalenderButton();
        hooks.login.enterDateTab(date);
        hooks.login.confirmDate();
        hooks.login.submitButton();
    }

    @Then("the all items catalog is displayed")
    public void allItemsCatalogIsDisplayed() {
        hooks.items.allItemsCheck();
    }

    @Then("the password validation message is {string}")
    public void passwordValidationMessageIs(String expectedMessage) {
        String actualMessage = hooks.login.passwordErrorValidation();
        if (!expectedMessage.equals(actualMessage)) {
            throw new AssertionError("Expected password validation '" + expectedMessage
                    + "' but was '" + actualMessage + "'");
        }
    }

    @When("I scroll to hotel {string}")
    public void scrollToHotel(String hotelName) {
        hooks.items.allItemsCheck();
        hooks.utils.scrollGesture(hotelName);
    }

    @Then("the Hotels tab is displayed and I scroll to {string}")
    public void hotelsTabIsDisplayedAndScrollTo(String hotelName) {
        hooks.items.allItemsCheck();
        if (!hooks.items.hotelsTab()) {
            throw new AssertionError("The Hotels tab was not displayed");
        }
        hooks.utils.scrollGesture(hotelName);
    }

    @When("I open the Holidays tab")
    public void openHolidaysTab() throws InterruptedException {
        if (!hooks.holiday.hotelsTab()) {
            throw new AssertionError("The Holidays tab was not enabled");
        }
    }

    @Then("the first holiday is displayed")
    public void firstHolidayIsDisplayed() {
        if (!hooks.holiday.firstElementOnHotelsTab()) {
            throw new AssertionError("The first holiday was not displayed");
        }
    }
}
