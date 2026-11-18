package com.example.seleniumportal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

class SeleniumPortalTest {

    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void successfulRegistrationShowsSuccessMessage() {
        register(uniqueUsername(), "Selenium123", "Selenium123", "CSE", "A", "Male");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage"))).isDisplayed());
    }

    @Test
    void failedRegistrationShowsPasswordMismatch() {
        register(uniqueUsername(), "Selenium123", "NotTheSame123", "IT", "B", "Female");
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("#confirmPassword ~ .invalid-feedback")))
                .getText().contains("Passwords do not match."));
    }

    @Test
    void successfulLoginOpensDashboard() {
        String username = uniqueUsername();
        register(username, "Selenium123", "Selenium123", "ECE", "C", "Other");
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys("Selenium123");
        driver.findElement(By.id("loginBtn")).click();
        assertEquals(username, wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("welcomeUsername"))).getText());
    }

    @Test
    void invalidLoginShowsErrorMessage() {
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("username")).sendKeys(uniqueUsername());
        driver.findElement(By.id("password")).sendKeys("wrong-password");
        driver.findElement(By.id("loginBtn")).click();
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginError"))).isDisplayed());
    }

    @Test
    void departmentAndSectionDropdownsCanBeSelected() {
        driver.get(baseUrl + "/register");
        new Select(driver.findElement(By.id("department"))).selectByValue("MECH");
        new Select(driver.findElement(By.id("section"))).selectByValue("D");
        assertEquals("MECH", new Select(driver.findElement(By.id("department"))).getFirstSelectedOption().getAttribute("value"));
        assertEquals("D", new Select(driver.findElement(By.id("section"))).getFirstSelectedOption().getAttribute("value"));
    }

    @Test
    void genderRadioButtonCanBeSelected() {
        driver.get(baseUrl + "/register");
        WebElement other = driver.findElement(By.id("genderOther"));
        other.click();
        assertTrue(other.isSelected());
    }

    @Test
    void logoutReturnsToHomeAndInvalidatesDashboardAccess() {
        String username = uniqueUsername();
        register(username, "Selenium123", "Selenium123", "CSE", "A", "Female");
        driver.get(baseUrl + "/login");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys("Selenium123");
        driver.findElement(By.id("loginBtn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));
        driver.findElement(By.id("logoutBtn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutMessage")));
        driver.get(baseUrl + "/dashboard");
        assertTrue(wait.until(ExpectedConditions.urlContains("/login")));
    }

    private void register(String username, String password, String confirmPassword,
                          String department, String section, String gender) {
        driver.get(baseUrl + "/register");
        driver.findElement(By.id("fullName")).sendKeys("Selenium Practice User");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("confirmPassword")).sendKeys(confirmPassword);
        driver.findElement(By.id("dob")).sendKeys("2000-01-15");
        driver.findElement(By.id(gender.equals("Male") ? "genderMale"
                : gender.equals("Female") ? "genderFemale" : "genderOther")).click();
        new Select(driver.findElement(By.id("department"))).selectByValue(department);
        new Select(driver.findElement(By.id("section"))).selectByValue(section);
        driver.findElement(By.id("receiveUpdates")).click();
        driver.findElement(By.id("registerBtn")).click();
    }

    private String uniqueUsername() {
        return "selenium-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
