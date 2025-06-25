import base.BaseTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatusCodeInterceptionTest extends BaseTest {

    @Test
    public void testMockedStatusCode() {

        String responseText = "Mocked Success Response";
        String responseBody = "<h3>" + responseText + "</h3>";

        Consumer<Route> mockStatusCodeHandler = route ->
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(200)
                        .setHeaders(Map.of("Content-Type", "text/html"))
                        .setBody(responseBody)
                );

        // Перехват запроса к /status_codes/404
        page.route("**/status_codes/404", mockStatusCodeHandler);

        page.navigate("https://the-internet.herokuapp.com/status_codes");

        Response response = page.waitForResponse(
                "**/status_codes/404",
                () -> page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("404")).click()
        );

        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("status_codes.png")));

        Assertions.assertAll(
                "Assert the response",
                () -> assertEquals(
                        200,
                        response.status(),
                        "Status code should be 200"),
                () -> assertEquals(
                        responseBody,
                        new String(response.body()),
                        "Response body should be '" + responseBody + "'"),
                () -> assertEquals(
                        responseText,
                        page.locator("h3").innerText(),
                        "The result page text should contain '" + responseText + "'")
        );
    }
}