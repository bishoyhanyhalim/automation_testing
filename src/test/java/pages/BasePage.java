package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.DriverManager;
import utils.HelperMethods;

public class BasePage {
    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    // Common elements
    private By menuButton = By.id("react-burger-menu-btn");
    private By logoutLink = By.id("logout_sidebar_link");
    private By cartIcon = By.className("shopping_cart_link");

    public void logout() {
        HelperMethods.click(menuButton);
        HelperMethods.click(logoutLink);
    }

    public void navigateToCart() {
        HelperMethods.click(cartIcon);
    }
}