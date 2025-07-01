import config.CustomOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;

@UsePlaywright(CustomOptions.class)
public class StatusCodeInterceptionTest {

    private static final String screenshotBasePath = "./target/screenshots/StatusCodeInterceptionTest_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/";

    @Disabled
    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    public void testMockedStatusCode(Page page) {

        String responseText = "Mocked Success Response";
        String responseBody = "<h3>" + responseText + "</h3>";

        Consumer<Route> mockStatusCodeHandler = route ->
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setHeaders(Map.of("Content-Type", "text/html"))
                        .setBody(responseBody)
                );

        // rule for request to /status_codes/404
        page.route("**/status_codes/404", mockStatusCodeHandler);

        page.navigate("https://the-internet.herokuapp.com/status_codes");

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("404")).click();
        page.waitForLoadState();

        Page.ScreenshotOptions screenshotOptions = new Page.ScreenshotOptions()
                .setPath(Paths.get(screenshotBasePath + "status_codes.png"))
                .setFullPage(true);
        page.screenshot(screenshotOptions);

        String expectedResult = responseText;
        String actualResult = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setLevel(3)).innerText();
        String errorText = "The result page text should contain '" + responseText + "'";

        Assertions.assertEquals(expectedResult, actualResult, errorText);
    }
}