package com.termix.app;

import android.app.Application;
import android.content.Context;

import com.termix.BuildConfig;
import com.termix.shared.errors.Error;
import com.termix.shared.logger.Logger;
import com.termix.shared.termux.TremixBootstrap;
import com.termix.shared.termux.TremixConstants;
import com.termix.shared.termux.crash.TremixCrashUtils;
import com.termix.shared.termux.file.TremixFileUtils;
import com.termix.shared.termux.settings.preferences.TremixAppSharedPreferences;
import com.termix.shared.termux.settings.properties.TremixAppSharedProperties;
import com.termix.shared.termux.shell.command.environment.TremixShellEnvironment;
import com.termix.shared.termux.shell.am.TremixAmSocketServer;
import com.termix.shared.termux.shell.TremixShellManager;
import com.termix.shared.termux.theme.TremixThemeUtils;

public class TremixApplication extends Application {

    private static final String LOG_TAG = "TremixApplication";

    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        // Set crash handler for the app
        TremixCrashUtils.setDefaultCrashHandler(this);

        // Set log config for the app
        setLogConfig(context);

        Logger.logDebug("Starting Application");

        // Set TremixBootstrap.TERMUX_APP_PACKAGE_MANAGER and TremixBootstrap.TERMUX_APP_PACKAGE_VARIANT
        TremixBootstrap.setTremixPackageManagerAndVariant(BuildConfig.TERMUX_PACKAGE_VARIANT);

        // Init app wide SharedProperties loaded from termux.properties
        TremixAppSharedProperties properties = TremixAppSharedProperties.init(context);

        // Init app wide shell manager
        TremixShellManager shellManager = TremixShellManager.init(context);

        // Set NightMode.APP_NIGHT_MODE
        TremixThemeUtils.setAppNightMode(properties.getNightMode());

        // Check and create termux files directory. If failed to access it like in case of secondary
        // user or external sd card installation, then don't run files directory related code
        Error error = TremixFileUtils.isTremixFilesDirectoryAccessible(this, true, true);
        boolean isTremixFilesDirectoryAccessible = error == null;
        if (isTremixFilesDirectoryAccessible) {
            Logger.logInfo(LOG_TAG, "Tremix files directory is accessible");

            error = TremixFileUtils.isAppsTremixAppDirectoryAccessible(true, true);
            if (error != null) {
                Logger.logErrorExtended(LOG_TAG, "Create apps/termux-app directory failed\n" + error);
                return;
            }

            // Setup termux-am-socket server
            TremixAmSocketServer.setupTremixAmSocketServer(context);
        } else {
            Logger.logErrorExtended(LOG_TAG, "Tremix files directory is not accessible\n" + error);
        }

        // Init TremixShellEnvironment constants and caches after everything has been setup including termux-am-socket server
        TremixShellEnvironment.init(this);

        if (isTremixFilesDirectoryAccessible) {
            TremixShellEnvironment.writeEnvironmentToFile(this);
        }
    }

    public static void setLogConfig(Context context) {
        Logger.setDefaultLogTag(TremixConstants.TERMUX_APP_NAME);

        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        TremixAppSharedPreferences preferences = TremixAppSharedPreferences.build(context);
        if (preferences == null) return;
        preferences.setLogLevel(null, preferences.getLogLevel());
    }

}
