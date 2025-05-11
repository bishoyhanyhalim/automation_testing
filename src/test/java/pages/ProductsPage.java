package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.HelperMethods;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    // Locators
    private final By productsTitle = By.className("title");
    private final By productImages = By.className("inventory_item_img");
    private final By productNames = By.className("inventory_item_name");
    private final By productPrices = By.className("inventory_item_price");
    private final By addToCartButtons = By.xpath("//button[contains(@id,'add-to-cart')]");
    private final By removeButtons = By.xpath("//button[contains(@id,'remove')]");
    private final By productDescriptions = By.className("inventory_item_desc");
    private final By sortDropdown = By.className("product_sort_container");
    private final By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // Page verification
    public boolean isAt() {
        try {
            WebElement titleElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(productsTitle));
            return titleElement.isDisplayed() && titleElement.getText().equals("Products");
        } catch (Exception e) {
            return false;
        }
    }

    // Product Images
    public List<WebElement> getAllProductImages() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(productImages));
        List<WebElement> images = HelperMethods.getElements(productImages);
        // Filter images to ensure they have a valid src
        images = images.stream()
                .filter(img -> {
                    String src = img.getAttribute("src");
                    boolean srcValid = src != null && !src.isEmpty();
                    return srcValid;
                })
                .collect(Collectors.toList());
        System.out.println("Found " + images.size() + " product images with valid src");
        return images;
    }

    // Product Names
    public List<WebElement> getAllProductNames() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNames));
        List<WebElement> names = HelperMethods.getElements(productNames);
        System.out.println("Found " + names.size() + " product names");
        return names;
    }

    // Product Prices
    public List<WebElement> getAllProductPrices() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productPrices));
        List<WebElement> prices = HelperMethods.getElements(productPrices);
        System.out.println("Found " + prices.size() + " product prices");
        return prices;
    }

    // Add to Cart Buttons
    public List<WebElement> getAllAddToCartButtons() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(addToCartButtons));
        List<WebElement> buttons = HelperMethods.getElements(addToCartButtons);
        System.out.println("Found " + buttons.size() + " Add to Cart buttons");
        return buttons;
    }

    // Product Descriptions
    public List<WebElement> getAllProductDescriptions() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productDescriptions));
        List<WebElement> descriptions = HelperMethods.getElements(productDescriptions);
        System.out.println("Found " + descriptions.size() + " product descriptions");
        return descriptions;
    }

    // Sorting functionality
    public void sortByNameAZ() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(dropdown).selectByValue("az");
        // Wait for the page to re-render after sorting
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNames));
        System.out.println("Sorted products by Name (A-Z)");
    }

    public void sortByPriceLowHigh() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(dropdown).selectByValue("lohi");
        // Wait for the page to re-render after sorting
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productPrices));
        System.out.println("Sorted products by Price (Low to High)");
    }

    // Verify sorting
    public boolean isSortedByNameAZ() {
        List<String> names = getAllProductNames().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        System.out.println("Product names after sorting: " + names);
        for (int i = 0; i < names.size() - 1; i++) {
            if (names.get(i).compareTo(names.get(i + 1)) > 0) {
                System.out.println("Sorting failed: " + names.get(i) + " comes after " + names.get(i + 1));
                return false;
            }
        }
        return true;
    }

    public boolean isSortedByPriceLowHigh() {
        List<Double> prices = getAllProductPrices().stream()
                .map(p -> {
                    String priceText = p.getText().replace("$", "").trim();
                    System.out.println("Price: " + priceText);
                    return Double.parseDouble(priceText);
                })
                .collect(Collectors.toList());

        System.out.println("Product prices after sorting: " + prices);
        for (int i = 0; i < prices.size() - 1; i++) {
            if (prices.get(i) > prices.get(i + 1)) {
                System.out.println("Sorting failed: " + prices.get(i) + " comes after " + prices.get(i + 1));
                return false;
            }
        }
        return true;
    }

    // Cart functionality
    public int getCartItemCount() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
            int count = Integer.parseInt(HelperMethods.getText(cartBadge));
            System.out.println("Cart item count: " + count);
            return count;
        } catch (Exception e) {
            System.out.println("Cart badge not found, assuming count is 0");
            return 0;
        }
    }

    public void addFirstProductToCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addToCartButtons));
        // Use JavaScript to click the button to avoid timing issues
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", addButton);
        System.out.println("Clicked Add to Cart for first product using JavaScript");
        // Wait for the button to change to "Remove"
        wait.until(ExpectedConditions.presenceOfElementLocated(removeButtons));
        System.out.println("Add to Cart button changed to Remove, product added successfully");
    }

    // Additional utility methods
    public String getFirstProductName() {
        List<WebElement> names = getAllProductNames();
        String name = names.get(0).getText();
        System.out.println("First product name: " + name);
        return name;
    }

    public String getFirstProductPrice() {
        List<WebElement> prices = getAllProductPrices();
        String price = prices.get(0).getText();
        System.out.println("First product price: " + price);
        return price;
    }

    public void addProductByName(String sauceLabsBikeLight) {
    }
}