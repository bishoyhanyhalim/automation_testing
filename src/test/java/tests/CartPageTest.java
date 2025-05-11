package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;
import utils.DriverManager;
import org.openqa.selenium.By;
import utils.HelperMethods;

import java.util.Objects;

public class CartPageTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
        driver.get("https://www.saucedemo.com/");
        System.out.println("Driver initialized successfully");
        System.out.println("Navigated to sauce demo.com");

        LoginPage loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);

        loginPage.login(
                ConfigReader.getProperty("username"),
                ConfigReader.getProperty("password")
        );
        System.out.println("Logged in with username: standard_user");

        Assert.assertTrue(productsPage.isAt(),
                "Products page was not loaded successfully after login");
        System.out.println("Products page loaded successfully");

        // Add "Sauce Labs Bike Light" to the cart directly
        By addToCartButton = By.id("add-to-cart-sauce-labs-bike-light");
        HelperMethods.waitForVisibility(addToCartButton);
        HelperMethods.clickWithRetry(addToCartButton, 3);
        System.out.println("Clicked Add to Cart for Sauce Labs Bike Light");

        // Wait for the cart badge to update to confirm addition
        HelperMethods.waitForVisibility(By.cssSelector("#shopping_cart_container > a > span"));
        HelperMethods.wait(2); // Reduced wait to 2 seconds
        System.out.println("Cart badge updated, count: " + cartPage.getCartItemCount());

        // Navigate to Cart page
        productsPage.navigateToCart();
        // Wait for the cart page to load and items to appear
        By cartItemName = By.xpath("//div[@class='inventory_item_name' and text()='Sauce Labs Bike Light']");
        HelperMethods.waitForVisibility(cartItemName); // Wait for a specific item
        HelperMethods.waitForVisibility(By.cssSelector("#cart_contents_container"));
        System.out.println("Navigated to Cart page after adding product. Current URL: " + driver.getCurrentUrl());

        // Verify navigation using URL directly
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("cart.html"),
                "Failed to navigate to Cart page (URL check failed)");
        System.out.println("Cart page loaded successfully");
    }

    @Test(priority = 1, description = "Verify the cart icon updates correctly after adding an item")
    public void verifyCartIconUpdatesOnAdd() {
        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Cart icon did not update correctly after adding an item");
        System.out.println("Cart badge updated after adding product, count: " + cartPage.getCartItemCount());
    }

    @Test(priority = 2, description = "Verify cart items are displayed correctly")
    public void verifyCartItemsDisplayedCorrectly() {
        Assert.assertFalse(cartPage.getCartItems().isEmpty(), "No items found in cart");
        Assert.assertFalse(cartPage.getItemNames().isEmpty(), "Item names are not displayed");
        Assert.assertFalse(cartPage.getItemPrices().isEmpty(), "Item prices are not displayed");
        System.out.println("Cart items verified: Names - " + cartPage.getItemNames() + ", Prices - " + cartPage.getItemPrices());
    }

    @Test(priority = 3, description = "Verify the Remove button functionality")
    public void verifyRemoveButtonFunctionality() {
        int initialCount = cartPage.getCartItems().size();
        cartPage.removeItem();
        HelperMethods.wait(1); // Reduced wait to 1 second
        Assert.assertEquals(cartPage.getCartItems().size(), initialCount - 1,
                "Remove button did not remove the item from cart");
        System.out.println("Removed item, new cart item count: " + cartPage.getCartItems().size());
    }

    @Test(priority = 4, description = "Verify the cart icon updates correctly when items are removed")
    public void verifyCartIconUpdatesOnRemove() {
        int initialCount = cartPage.getCartItemCount();
        cartPage.removeItem();
        HelperMethods.wait(1); // Reduced wait to 1 second
        Assert.assertEquals(cartPage.getCartItemCount(), initialCount - 1,
                "Cart icon did not update correctly after removing an item");
        System.out.println("Cart icon updated after remove, count: " + cartPage.getCartItemCount());
    }

    @Test(priority = 5, description = "Verify the cart count resets after removing all items")
    public void verifyCartCountResets() {
        cartPage.removeAllItems();
        HelperMethods.wait(1); // Reduced wait to 1 second
        Assert.assertEquals(cartPage.getCartItemCount(), 0,
                "Cart count did not reset to 0 after removing all items");
        System.out.println("Cart count reset to: " + cartPage.getCartItemCount());
    }

    @Test(priority = 6, description = "Verify Continue Shopping button navigates back to the product page")
    public void verifyContinueShoppingNavigation() {
        cartPage.clickContinueShopping();
        HelperMethods.wait(1); // Reduced wait to 1 second
        Assert.assertTrue(productsPage.isAt(), "Continue Shopping did not navigate to Products page");
        System.out.println("Navigated back to Products page via Continue Shopping");
    }

    @Test(priority = 7, description = "Verify Checkout button redirects to the checkout page")
    public void verifyCheckoutNavigation() {
        cartPage.clickCheckout();
        Assert.assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("checkout-step-one.html"),
                "Checkout button did not redirect to the checkout page");
        System.out.println("Redirected to checkout page. Current URL: " + driver.getCurrentUrl());
    }
}