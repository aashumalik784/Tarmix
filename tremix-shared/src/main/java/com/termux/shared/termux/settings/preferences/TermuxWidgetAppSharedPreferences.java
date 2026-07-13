package com.termix.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termix.shared.logger.Logger;
import com.termix.shared.android.PackageUtils;
import com.termix.shared.settings.preferences.AppSharedPreferences;
import com.termix.shared.settings.preferences.SharedPreferenceUtils;
import com.termix.shared.termux.TremixUtils;
import com.termix.shared.termux.settings.preferences.TremixPreferenceConstants.TERMUX_WIDGET_APP;
import com.termix.shared.termux.TremixConstants;

import java.util.UUID;

public class TremixWidgetAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "TremixWidgetAppSharedPreferences";

    private TremixWidgetAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                TremixConstants.TERMUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                TremixConstants.TERMUX_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link TremixWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TremixConstants#TERMUX_WIDGET_PACKAGE_NAME}.
     * @return Returns the {@link TremixWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TremixWidgetAppSharedPreferences build(@NonNull final Context context) {
        Context termuxWidgetPackageContext = PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_WIDGET_PACKAGE_NAME);
        if (termuxWidgetPackageContext == null)
            return null;
        else
            return new TremixWidgetAppSharedPreferences(termuxWidgetPackageContext);
    }

    /**
     * Get the {@link TremixWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TremixConstants#TERMUX_WIDGET_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TremixWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TremixWidgetAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context termuxWidgetPackageContext = TremixUtils.getContextForPackageOrExitApp(context, TremixConstants.TERMUX_WIDGET_PACKAGE_NAME, exitAppOnError);
        if (termuxWidgetPackageContext == null)
            return null;
        else
            return new TremixWidgetAppSharedPreferences(termuxWidgetPackageContext);
    }



    public static String getGeneratedToken(@NonNull Context context) {
        TremixWidgetAppSharedPreferences preferences = TremixWidgetAppSharedPreferences.build(context, true);
        if (preferences == null) return null;
        return preferences.getGeneratedToken();
    }

    public String getGeneratedToken() {
        String token =  SharedPreferenceUtils.getString(mSharedPreferences, TERMUX_WIDGET_APP.KEY_TOKEN, null, true);
        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferenceUtils.setString(mSharedPreferences, TERMUX_WIDGET_APP.KEY_TOKEN, token, true);
        }
        return token;
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, TERMUX_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_WIDGET_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
