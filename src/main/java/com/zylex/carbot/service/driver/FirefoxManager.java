package com.zylex.carbot.service.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.zylex.carbot.CarbotApplication.HEADLESS_DRIVER;

@Service
public class FirefoxManager extends DriverManager {

    public void initiateDriver() {
        quitDriver();
        WebDriverManager.firefoxdriver().setup();
        setupLogging();
        FirefoxOptions options = new FirefoxOptions();
        driver = HEADLESS_DRIVER
                ? new FirefoxDriver(options.addArguments("--headless"))
                : new FirefoxDriver();
        manageDriver();
        wait = new WebDriverWait(driver, waitTimeout, 100);
        logger.logDriver();
    }

    private void setupLogging() {
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        logger.startLogMessage();
    }
}
