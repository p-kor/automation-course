package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

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
        Optional.ofNullable(page).ifPresent(Page::close);
        Optional.ofNullable(context).ifPresent(BrowserContext::close);
        Optional.ofNullable(browser).ifPresent(Browser::close);
        Optional.ofNullable(playwright).ifPresent(Playwright::close);
    }
}