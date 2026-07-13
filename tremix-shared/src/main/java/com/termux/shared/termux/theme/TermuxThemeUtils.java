package com.termix.shared.termux.theme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termix.shared.termux.settings.properties.TremixPropertyConstants;
import com.termix.shared.termux.settings.properties.TremixSharedProperties;
import com.termix.shared.theme.NightMode;

public class TremixThemeUtils {

    /** Get the {@link TremixPropertyConstants#KEY_NIGHT_MODE} value from the properties file on disk
     * and set it to app wide night mode value. */
    public static void setAppNightMode(@NonNull Context context) {
        NightMode.setAppNightMode(TremixSharedProperties.getNightMode(context));
    }

    /** Set name as app wide night mode value. */
    public static void setAppNightMode(@Nullable String name) {
        NightMode.setAppNightMode(name);
    }

}
