package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HelperMethods {
    private static final int WAIT_TIMEOUT_SECONDS = 3;

    public static void waitForVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void clickWithRetry(By locator, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                waitForVisibility(locator);
                DriverManager.getDriver().findElement(locator).click();
                break;
            } catch (Exception e) {
                attempts++;
                if (attempts == maxRetries) throw e;
            }
        }
    }

    public static void click(By locator) {
        waitForVisibility(locator);
        DriverManager.getDriver().findElement(locator).click();
    }

    public static String getText(By locator) {
        waitForVisibility(locator);
        return DriverManager.getDriver().findElement(locator).getText();
    }

    public static List<WebElement> getElements(By locator) {
        waitForVisibility(locator);
        return DriverManager.getDriver().findElements(locator);
    }

    public static void wait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // New method to check if an element is present in DOM
    public static boolean isElementPresent(By locator) {
        return !DriverManager.getDriver().findElements(locator).isEmpty();
    }
}