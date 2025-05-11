package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.HelperMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartPage extends BasePage {
    // Locators
    private final By cartItems = By.cssSelector(".cart_item");
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By itemPrices = By.cssSelector(".inventory_item_price");
    private final By removeButton = By.id("remove-sauce-labs-bike-light");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");
    private final By cartBadge = By.cssSelector("#shopping_cart_container > a > span");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // Verify Cart page is loaded
    public boolean isAt() {
        return driver.getCurrentUrl().contains("cart.html");
    }

    // Get all cart items
    public List<WebElement> getCartItems() {
        try {
            return HelperMethods.getElements(cartItems); // Use getElements directly, handle presence
        } catch (Exception e) {
            System.out.println("No cart items found: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get item names
    public List<String> getItemNames() {
        return getCartItems().stream()
                .map(item -> item.findElement(itemNames).getText())
                .collect(Collectors.toList());
    }

    // Get item prices
    public List<String> getItemPrices() {
        return getCartItems().stream()
                .map(item -> item.findElement(itemPrices).getText())
                .collect(Collectors.toList());
    }

    // Remove item (using the specific remove button)
    public void removeItem() {
        if (HelperMethods.isElementPresent(removeButton)) {
            HelperMethods.clickWithRetry(removeButton, 3); // Retry up to 3 times
        } else {
            System.out.println("Remove button not found, cart might be empty");
        }
    }

    // Remove all items
    public void removeAllItems() {
        List<WebElement> items = getCartItems();
        while (!items.isEmpty()) {
            removeItem();
            items = getCartItems(); // Refresh the list after removal
        }
    }

    // Click Continue Shopping
    public void clickContinueShopping() {
        HelperMethods.click(continueShoppingButton);
    }

    // Click Checkout
    public void clickCheckout() {
        HelperMethods.click(checkoutButton);
    }

    // Get cart item count from badge
    public int getCartItemCount() {
        try {
            HelperMethods.waitForVisibility(cartBadge); // Wait for badge to be visible
            return Integer.parseInt(HelperMethods.getText(cartBadge));
        } catch (Exception e) {
            System.out.println("Error getting cart badge count: " + e.getMessage());
            return 0; // Return 0 if badge is not visible (cart is empty)
        }
    }

    // Check if cart is empty
    public boolean isCartEmpty() {
        return getCartItems().isEmpty();
    }
}