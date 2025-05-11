package tests;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;
import utils.DriverManager;

import java.util.List;

public class ProductsTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
        driver.get("https://www.saucedemo.com/");
        System.out.println("Navigated to saucedemo.com");

        // Initialize pages
        LoginPage loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);

        // Perform login
        loginPage.login(
                ConfigReader.getProperty("username"),
                ConfigReader.getProperty("password")
        );
        System.out.println("Logged in with username: standard_user");

        // Verify products page is loaded
        Assert.assertTrue(productsPage.isAt(),
                "Products page was not loaded successfully after login");
        System.out.println("Products page loaded successfully");
    }

    @Test(priority = 1, description = "Verify all product images are displayed")
    public void verifyProductImagesDisplay() {
        List<WebElement> images = productsPage.getAllProductImages();
        Assert.assertFalse(images.isEmpty(), "No product images found");
        Assert.assertEquals(images.size(), 6, "Expected 6 product images, but found " + images.size());

        boolean allImagesDisplayed = images.stream()
                .allMatch(img -> {
                    boolean displayed = img.isDisplayed();
                    String src = img.getAttribute("src");
                    boolean srcValid = src != null && !src.isEmpty();
                    System.out.println("Image displayed: " + displayed + ", src: " + src);
                    return displayed && srcValid;
                });

        Assert.assertTrue(allImagesDisplayed,
                "Not all product images are displayed correctly or have invalid src");
    }

    @Test(priority = 2, description = "Verify product names are displayed and not empty")
    public void verifyProductNames() {
        List<WebElement> names = productsPage.getAllProductNames();
        Assert.assertFalse(names.isEmpty(), "No product names found");

        boolean allNamesValid = names.stream()
                .allMatch(name -> {
                    boolean displayed = name.isDisplayed();
                    String text = name.getText();
                    boolean notEmpty = !text.trim().isEmpty();
                    System.out.println("Name: " + text + ", displayed: " + displayed);
                    return displayed && notEmpty;
                });

        Assert.assertTrue(allNamesValid,
                "Some product names are missing, empty, or not displayed");
    }

    @Test(priority = 3, description = "Verify product prices format")
    public void verifyProductPrices() {
        List<WebElement> prices = productsPage.getAllProductPrices();
        Assert.assertFalse(prices.isEmpty(), "No product prices found");

        boolean allPricesValid = prices.stream()
                .allMatch(price -> {
                    boolean displayed = price.isDisplayed();
                    String priceText = price.getText();
                    boolean matchesFormat = priceText.matches("^\\$\\d+\\.\\d{2}$");
                    System.out.println("Price: " + priceText + ", displayed: " + displayed + ", matches format: " + matchesFormat);
                    return displayed && matchesFormat;
                });

        Assert.assertTrue(allPricesValid,
                "Some product prices have invalid format or are not displayed");
    }

    @Test(priority = 4, description = "Verify Add to Cart buttons are displayed and enabled")
    public void verifyAddToCartButtons() {
        List<WebElement> buttons = productsPage.getAllAddToCartButtons();
        Assert.assertFalse(buttons.isEmpty(), "No Add to Cart buttons found");

        boolean allButtonsFunctional = buttons.stream()
                .allMatch(btn -> {
                    boolean displayed = btn.isDisplayed();
                    boolean enabled = btn.isEnabled();
                    System.out.println("Add to Cart button displayed: " + displayed + ", enabled: " + enabled);
                    return displayed && enabled;
                });

        Assert.assertTrue(allButtonsFunctional,
                "Some Add to Cart buttons are not displayed or enabled");
    }

    @Test(priority = 5, description = "Verify sorting by Name (A-Z)")
    public void verifySortingAZ() {
        productsPage.sortByNameAZ();
        Assert.assertTrue(productsPage.isSortedByNameAZ(),
                "Products are not sorted correctly by Name (A-Z)");
    }

    @Test(priority = 6, description = "Verify sorting by Price (low to high)")
    public void verifySortingPriceLowHigh() {
        productsPage.sortByPriceLowHigh();
        Assert.assertTrue(productsPage.isSortedByPriceLowHigh(),
                "Products are not sorted correctly by Price (low to high)");
    }

    @Test(priority = 7, description = "Verify adding product to cart")
    public void verifyAddToCartFunctionality() {
        int initialCount = productsPage.getCartItemCount();
        System.out.println("Initial cart count: " + initialCount);
        productsPage.addFirstProductToCart();
        int newCount = productsPage.getCartItemCount();
        System.out.println("New cart count: " + newCount);
        Assert.assertEquals(newCount, initialCount + 1,
                "Cart count did not increase after adding product");
    }

    @Test(priority = 8, description = "Verify product descriptions are displayed and not empty")
    public void verifyProductDescriptions() {
        List<WebElement> descriptions = productsPage.getAllProductDescriptions();
        Assert.assertFalse(descriptions.isEmpty(), "No product descriptions found");

        boolean allDescriptionsValid = descriptions.stream()
                .allMatch(desc -> {
                    boolean displayed = desc.isDisplayed();
                    String text = desc.getText();
                    boolean notEmpty = !text.trim().isEmpty();
                    System.out.println("Description: " + text + ", displayed: " + displayed);
                    return displayed && notEmpty;
                });

        Assert.assertTrue(allDescriptionsValid,
                "Some product descriptions are missing, empty, or not displayed");
    }
}