package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;

public class iLoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        driver.get("https://www.saucedemo.com/"); // 

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isAt(), "Failed to verify Products page is displayed");
    }
}
