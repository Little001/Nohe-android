package nohe.nohe_android.activity.controllers;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import java.util.Locale;
import nohe.nohe_android.activity.app.AppConfig;

public class LocaleController {
    private AppCompatActivity context;

    public LocaleController(AppCompatActivity activity) {
        this.context = activity;
    }

    public void setEnglishLocale() {
        String locale = "de";
        setLocaleIfNeededLocale(locale);
    }

    public void setCzechLocale() {
        String locale = "cz";
        setLocaleIfNeededLocale(locale);
    }

    private void setLocaleIfNeededLocale(String language) {
        if (AppConfig.CURRENT_LANGUAGE.equals(language)) {
            return;
        }
        AppConfig.CURRENT_LANGUAGE = language;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.context.getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        this.context.getBaseContext().getResources().updateConfiguration(config,
                this.context.getBaseContext().getResources().getDisplayMetrics());

        this.context.recreate();
    }
}
