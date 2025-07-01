package config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

public class CustomOptions implements OptionsFactory {

    @Override
    public Options getOptions() {
        return new Options()
                .setBrowserName("chromium")
                .setHeadless(true)
                .setContextOptions(new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080));
    }
}
