import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.junit.UsePlaywright;
import config.CustomOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright(CustomOptions.class)
public class GitHubSearchInterceptionTest {

    private static final String screenshotBasePath = "./target/screenshots/GitHubSearchInterceptionTest_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/";

    @BeforeEach
    void setRouting(BrowserContext context) {
        context.route("**/search**", route -> {
            if (!route.request().resourceType().equals("document") &&
                    !route.request().resourceType().equals("xhr")) {
                route.resume();
                return;
            }
            String originalUrl = route.request().url();
            System.out.println("Original URL: " + originalUrl);
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";
            System.out.println("Modified URL: " + modifiedUrl);
            route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
        });
    }

    @AfterEach
    void removeRouting(BrowserContext context) {
        context.unrouteAll();
    }

    @Test
    void testSearchModification(Page page) {

        String pageUrl = "https://github.com/search?q=java";
        page.navigate(pageUrl);
        page.getByTestId("results-list").waitFor();

        Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotBasePath + "gitHubSearch.png"))
                .setFullPage(true);
        page.screenshot(screenshotOptions);

        String expectedQueryText = "stars:>10000";
        String actualQueryText = page.locator("button.header-search-button span[data-target='qbsearch-input.inputButtonText']").innerText();

        assertEquals(expectedQueryText, actualQueryText, "Query should be " + expectedQueryText);
    }
}
