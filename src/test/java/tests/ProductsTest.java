package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.ConfigReader;
import utils.DriverManager;
import java.time.Duration;
import java.util.List;
import static org.testng.Assert.assertEquals;

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
        assertEquals(images.size(), 6, "Expected 6 product images, but found " + images.size());

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
        assertEquals(newCount, initialCount + 1,
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

    @Test(priority = 9, description = "Verify Remove button appears after adding to cart and decreases cart count")
    public void verifyRemoveButtonFunctionality() {
        // Step 1: Add a product to cart (if not already added)
        if (productsPage.getCartItemCount() == 0) {
            productsPage.addFirstProductToCart();
        }
        int initialCount = productsPage.getCartItemCount();
        System.out.println("Initial cart count: " + initialCount);

        // Step 2: Verify Remove button is displayed for the added product
        WebElement removeButton = driver.findElement(By.xpath("//*[@id='remove-sauce-labs-backpack']"));
        Assert.assertTrue(removeButton.isDisplayed(),
                "Remove button is not displayed after adding product to cart");
        System.out.println("Remove button is displayed");

        // Step 3: Click the Remove button
        removeButton.click();
        System.out.println("Clicked Remove button");

        // Step 4: Verify cart count decreases by 1
        int updatedCount = productsPage.getCartItemCount();
        assertEquals(updatedCount, initialCount - 1,
                "Cart count did not decrease after removing product");
        System.out.println("Updated cart count: " + updatedCount);

        // Step 5: Verify Remove button changes back to "Add to Cart"
        WebElement addButton = driver.findElement(By.xpath("//*[@id='add-to-cart-sauce-labs-backpack']"));
        Assert.assertTrue(addButton.isDisplayed(),
                "Add to Cart button did not reappear after removal");
        System.out.println("Add to Cart button is displayed again");
    }

    @Test(priority = 10, description = "Verify 'Reset App State' persists after page refresh")
    public void verifyResetAppStateFunctionality() {
        // Temporary page methods (would normally be in ProductsPage)
        class TempPageExtensions {
            public void openMenu() {
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.id("react-burger-menu-btn")
                        )).click();
            }

            public void resetAppState() {
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.id("reset_sidebar_link")
                        )).click();
            }

            public void refreshPage() {
                driver.navigate().refresh();
                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.className("inventory_item")
                        ));
            }
        }
        TempPageExtensions page = new TempPageExtensions();

        // 1. Add multiple items
        productsPage.addFirstProductToCart();
        productsPage.getAllAddToCartButtons().get(3).click(); // Add another item
        int initialCount = productsPage.getCartItemCount();
        assertEquals(initialCount, 2, "Should have 2 items in cart");

        // 2. Reset app state
        page.openMenu();
        page.resetAppState();

        // 3. Verify immediate reset
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(d -> productsPage.getCartItemCount() == 0);
        System.out.println("Cart reset verified before refresh");

        // 4. Refresh and verify persistence
        page.refreshPage();
        int postRefreshCount = productsPage.getCartItemCount();
        assertEquals(postRefreshCount, 0,
                "Cart should remain empty after refresh");
        System.out.println("Reset state persisted after refresh");

        // 5. Verify all buttons reset
        long removeButtons = driver.findElements(By.xpath("//button[text()='REMOVE']"))
                .size();
        assertEquals(removeButtons, 0,
                "All buttons should show 'ADD TO CART' after reset + refresh");
    }


    @Test(priority = 11, description = "Verify product details page navigation and content")
    public void verifyProductDetailsPage() {
        // 1. Store product info from inventory list
        WebElement firstProduct = driver.findElement(By.cssSelector(".inventory_item:first-child"));
        String expectedName = firstProduct.findElement(By.cssSelector(".inventory_item_name")).getText();
        String expectedPrice = firstProduct.findElement(By.cssSelector(".inventory_item_price")).getText();
        String expectedDesc = firstProduct.findElement(By.cssSelector(".inventory_item_desc")).getText();

        // 2. Click product name to navigate to details
        firstProduct.findElement(By.cssSelector(".inventory_item_name")).click();

        // 3. Verify details page elements
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.urlContains("inventory-item.html"));

        Assert.assertEquals(
                driver.findElement(By.cssSelector(".inventory_details_name")).getText(),
                expectedName,
                "Product name mismatch"
        );

        Assert.assertEquals(
                driver.findElement(By.cssSelector(".inventory_details_price")).getText(),
                expectedPrice,
                "Product price mismatch"
        );

        Assert.assertEquals(
                driver.findElement(By.cssSelector(".inventory_details_desc")).getText(),
                expectedDesc,
                "Product description mismatch"
        );

        // 4. Verify back button returns to products
        driver.findElement(By.id("back-to-products")).click();
        wait.until(ExpectedConditions.urlMatches(".*inventory.html$"));
    }

    @Test(priority = 13, description = "Verify shopping cart badge counter updates correctly")
    public void verifyCartBadgeCounter() {
        // 1. Reset state by removing all items
        driver.findElements(By.cssSelector(".btn_inventory"))
                .forEach(btn -> {
                    if(btn.getText().equals("REMOVE")) {
                        btn.click();
                    }
                });

        // 2. Verify badge is empty initially
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        Assert.assertTrue(badges.isEmpty(), "Cart badge should be hidden when empty");

        // 3. Add items and verify counter
        driver.findElements(By.cssSelector(".btn_inventory"))
                .stream()
                .limit(5)
                .forEach(btn -> btn.click());

        // 4. Verify badge shows correct count
        int badgeCount = Integer.parseInt(
                driver.findElement(By.cssSelector(".shopping_cart_badge")).getText()
        );
        Assert.assertEquals(badgeCount, 5, "Cart badge shows wrong quantity");

        // 5. Remove one item and verify update
        driver.findElement(By.cssSelector(".btn_inventory")).click();
        badgeCount = Integer.parseInt(
                driver.findElement(By.cssSelector(".shopping_cart_badge")).getText()
        );
        Assert.assertEquals(badgeCount, 4, "Cart badge didn't update after removal");
    }

}