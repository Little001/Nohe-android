package nohe.nohe_android.activity.controllers;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Locale;
import nohe.nohe_android.activity.app.AppConfig;

public class LocaleController {
    private AppCompatActivity context;
    private Button czBtn;
    private Button enBtn;

    public LocaleController(AppCompatActivity activity) {
        this.context = activity;
        /*this.czBtn = czBtn;
        this.enBtn = enBtn;*/
        // initLocale();
    }

    public void setEnglishLocale() {
        String locale = "de";
        czBtn.setVisibility(View.VISIBLE);
        enBtn.setVisibility(View.INVISIBLE);

        setLocaleIfNeededLocale(locale);
    }

    public void setCzechLocale() {
        String locale = "cs";
        enBtn.setVisibility(View.VISIBLE);
        czBtn.setVisibility(View.INVISIBLE);

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

    private void initLocale() {
        if (AppConfig.CURRENT_LANGUAGE.equals("cs")) {
            this.setCzechLocale();
        } else {
            this.setEnglishLocale();
        }
    }
}
