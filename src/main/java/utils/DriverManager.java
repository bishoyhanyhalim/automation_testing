package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null || ((ChromeDriver) driver).getSessionId() == null) {
            // Configure Chrome options
            ChromeOptions options = createChromeOptions();

            // Setup WebDriverManager and create driver with options
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);

            // Maximize window
            driver.manage().window().maximize();
        }
        return driver;
    }

    /**
     * Creates ChromeOptions with settings for SourceDemo website
     * @return ChromeOptions with custom settings
     */
    private static ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // Disable password-related popups (including "Change your password")
        Map<String, Object> prefs = new HashMap<>();

        // Disable Chrome's password manager entirely
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        // Disable password-saving prompts
        prefs.put("profile.default_content_settings.password_manager_enabled", false);
        prefs.put("profile.default_content_settings.password_saving_disabled", true);

        // Disable "Change password" prompts (new in Chrome 90+)
        prefs.put("profile.password_protection_enabled", false);
        prefs.put("profile.password_bubble_enabled", false);

        // Disable autofill and password-related popups
        prefs.put("autofill.enabled", false);
        prefs.put("autofill.profile_enabled", false);

        // Disable Chrome's built-in password leak detection (which may trigger popups)
        prefs.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", prefs);

        // Additional Chrome arguments to suppress popups
        options.addArguments(
                "--disable-infobars",
                "--disable-popup-blocking",
                "--disable-notifications",
                "--disable-save-password-bubble",  // Explicitly disable password save prompts
                "--disable-autofill-keyboard-accessory-view", // Disable autofill suggestions
                "--disable-features=PasswordChange,PasswordLeakDetection" // Disable password-related features
        );

        // Exclude switches to hide automation-related UI
        options.setExperimentalOption("excludeSwitches",
                new String[]{
                        "enable-automation",
                        "load-extension",
                        "ignore-certificate-errors"
                }
        );

        return options;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

}