package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DebetCardTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
// предварительно копируем в папку tmp файл chromedriver.exe из архива
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
// опции для управления режимами работы с памятью, будут полезны при запуске тестов в CI
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
// опция для включения headless-режима, обязателен при запуске тестов в CI
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

        driver.get("http://localhost:9999");
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
        driver = null;
    }

    // ТЕСТ С ВАЛИДНЫМИ ДАННЫМИ
    @Test
    public void shouldSubmitRequest() {
       driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Смирнов Иван");
       driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79270000000");
       driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
       driver.findElement(By.cssSelector("button.button")).click();
       WebElement result = driver.findElement(By.cssSelector("[data-test-id='order-success']"));
       assertTrue(result.isDisplayed());
       assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", result.getText().trim());

    }

    // ТЕСТЫ НА ВАЛИДАЦИЮ ПОЛЯ "ИМЯ"
    @Test
    public void shouldGetErrorIfNameInEnglish() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan Petrov");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79270000000");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement errorElement = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(errorElement.isDisplayed());
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", errorElement.getText().trim());
    }

    @Test
    public void shouldGetErrorIfNameIsEmpty() {
        // Поле Имя оставляем пустым
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79270000000");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement errorElement = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertTrue(errorElement.isDisplayed());
        assertEquals("Поле обязательно для заполнения", errorElement.getText().trim());
    }

    // ТЕСТЫ НА ВАЛИДАЦИЮ ПОЛЯ "ТЕЛЕФОН"
    @Test
    public void shouldGetErrorIfPhoneIsInvalid() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Смирнов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("89270000000"); // Без плюс-семь
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement errorElement = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(errorElement.isDisplayed());
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", errorElement.getText().trim());
    }

    @Test
    public void shouldGetErrorIfPhoneIsEmpty() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Смирнов Иван");
        // Поле Телефон оставляем пустым
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        WebElement errorElement = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertTrue(errorElement.isDisplayed());
        assertEquals("Поле обязательно для заполнения", errorElement.getText().trim());
    }

    // ТЕСТ НА ЧЕКБОКС СОГЛАСИЯ
    @Test
    public void shouldGetErrorIfNoAgreement() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Смирнов Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79270000000");
        // Чекбокс НЕ кликаем
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем, что у блока согласия появился класс ошибки (обычно текст ошибки тут не появляется, только цвет)
        WebElement agreementBlock = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid"));
        assertTrue(agreementBlock.isDisplayed());
    }
}
