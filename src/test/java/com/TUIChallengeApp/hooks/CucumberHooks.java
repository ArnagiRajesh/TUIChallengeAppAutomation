package com.TUIChallengeApp.hooks;

import java.io.IOException;

import com.TUIChallengeApp.TUIChallenge.AllItems;
import com.TUIChallengeApp.TUIChallenge.HolodaysTab;
import com.TUIChallengeApp.TUIChallenge.LoginPage;
import com.TUIChallengeApp.Utils.AndroidUtils;
import com.TUIChallengeApp.Utils.BaseTest;

import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;

public class CucumberHooks {

    public AndroidDriver driver;
    public LoginPage login;
    public AllItems items;
    public AndroidUtils utils;
    public HolodaysTab holiday;

    @BeforeAll
    public static void startAppiumServer() throws IOException {
        BaseTest.startService();
    }

    @Before
    public void startScenario() throws IOException {
        driver = new BaseTest().createDriver();
        login = new LoginPage(driver);
        items = new AllItems(driver);
        utils = new AndroidUtils(driver);
        holiday = new HolodaysTab(driver);
        driver.terminateApp("com.tui.qa.challenge");
        driver.activateApp("com.tui.qa.challenge");
    }

    @After
    public void stopScenario() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void stopAppiumServer() {
        BaseTest.stopService();
    }
}
