package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    protected void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    protected void tearDown() {
        if (Objects.nonNull(page)) {
            page.close();
        }
        if (Objects.nonNull(context)) {
            context.close();
        }
        if (Objects.nonNull(browser)) {
            browser.close();
        }
        if (Objects.nonNull(playwright)) {
            playwright.close();
        }
    }
}