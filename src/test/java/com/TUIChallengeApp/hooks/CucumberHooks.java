package com.TUIChallengeApp.hooks;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.TUIChallengeApp.TUIChallenge.AllItems;
import com.TUIChallengeApp.TUIChallenge.HolidaysPage;
import com.TUIChallengeApp.TUIChallenge.LoginPage;
import com.TUIChallengeApp.Utils.BaseTest;

import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;

public class CucumberHooks {

    private static final Logger LOG = LoggerFactory.getLogger(CucumberHooks.class);

    public AndroidDriver driver;
    public LoginPage login;
    public AllItems items;
    public HolidaysPage holidays;

    @BeforeAll
    public static void startAppiumServer() throws IOException {
        BaseTest.startService();
    }

    @Before
    public void startScenario() throws IOException {
        try {
            driver = new BaseTest().createDriver();
            login = new LoginPage(driver);
            items = new AllItems(driver);
            holidays = new HolidaysPage(BaseTest.getDriver());
            LOG.atInfo().addKeyValue("event", "scenario_started")
                    .addKeyValue("thread", Thread.currentThread().getName()).log("Scenario started");
        } catch (IOException | RuntimeException exception) {
            LOG.atError().addKeyValue("event", "scenario_setup_failed")
                    .addKeyValue("thread", Thread.currentThread().getName()).setCause(exception)
                    .log("Scenario setup failed");
            BaseTest.quitDriver();
            throw exception;
        }
    }

    @After
    public void stopScenario() {
        try {
            BaseTest.quitDriver();
        } finally {
            driver = null;
            login = null;
            items = null;
            holidays = null;
        }
    }

    @AfterAll
    public static void stopAppiumServer() {
        BaseTest.stopService();
    }
}
