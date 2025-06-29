package config;

import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

public class CustomOptions implements OptionsFactory {

    @Override
    public Options getOptions() {
        return new Options().setHeadless(true);
    }
}
