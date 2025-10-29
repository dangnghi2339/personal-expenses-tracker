package com.example.dack1.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public final class LocaleUtil {
    private LocaleUtil() {}
    public static ContextWrapper wrap(Context base, String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) languageCode = "en";
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = base.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);
            Context localized = base.createConfigurationContext(config);
            return new ContextWrapper(localized);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return new ContextWrapper(base);
        }
    }
}
