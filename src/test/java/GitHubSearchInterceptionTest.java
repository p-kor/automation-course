import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.junit.UsePlaywright;
import config.CustomOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";
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

        page.getByTestId("results-list").locator(".search-title").first().waitFor();
        checkTextInSearchButton(page, "stars:>10000");

        page.getByTestId("results-list").locator(".search-title").last().waitFor();
        checkStarsCount(page, 10_000);
    }

    private void checkStarsCount (Page page, int expectedCount) {

        Predicate<Locator> checkStarCount = resultItem -> {
            double actual = 0;
            String starsCount = resultItem.first().innerText().trim();
            if (starsCount.contains("k")) {
                starsCount = starsCount.replace("k", "");
                actual = Double.parseDouble(starsCount) * 1000;
            } else {
                actual = Integer.parseInt(starsCount);
            }
            return actual > expectedCount;
        };

        boolean result = page.getByTestId("results-list")
                .locator("div.flszRz")
                .locator("a[href$='/stargazers'] span")
                .all()
                .stream()
                .allMatch(checkStarCount);

        assertTrue(result, "All results should have more than " + expectedCount + " stars");
    }

    private void checkTextInSearchButton (Page page, String expectedText) {

        String actualText =
                page.locator("button.header-search-button span[data-target='qbsearch-input.inputButtonText']")
                        .innerText();

        assertEquals(expectedText, actualText, "Query should be " + expectedText);
    }
}
