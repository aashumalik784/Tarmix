package com.termix.shared.termux.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.termix.shared.errors.Error;
import com.termix.shared.file.FileUtils;
import com.termix.shared.logger.Logger;
import com.termix.shared.shell.command.ExecutionCommand;
import com.termix.shared.shell.command.environment.AndroidShellEnvironment;
import com.termix.shared.shell.command.environment.ShellEnvironmentUtils;
import com.termix.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.termix.shared.termux.TremixBootstrap;
import com.termix.shared.termux.TremixConstants;
import com.termix.shared.termux.shell.TremixShellUtils;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Environment for Tremix.
 */
public class TremixShellEnvironment extends AndroidShellEnvironment {

    private static final String LOG_TAG = "TremixShellEnvironment";

    /** Environment variable for the termux {@link TremixConstants#TERMUX_PREFIX_DIR_PATH}. */
    public static final String ENV_PREFIX = "PREFIX";

    public TremixShellEnvironment() {
        super();
        shellCommandShellEnvironment = new TremixShellCommandShellEnvironment();
    }


    /** Init {@link TremixShellEnvironment} constants and caches. */
    public synchronized static void init(@NonNull Context currentPackageContext) {
        TremixAppShellEnvironment.setTremixAppEnvironment(currentPackageContext);
    }

    /** Init {@link TremixShellEnvironment} constants and caches. */
    public synchronized static void writeEnvironmentToFile(@NonNull Context currentPackageContext) {
        HashMap<String, String> environmentMap = new TremixShellEnvironment().getEnvironment(currentPackageContext, false);
        String environmentString = ShellEnvironmentUtils.convertEnvironmentToDotEnvFile(environmentMap);

        // Write environment string to temp file and then move to final location since otherwise
        // writing may happen while file is being sourced/read
        Error error = FileUtils.writeTextToFile("termux.env.tmp", TremixConstants.TERMUX_ENV_TEMP_FILE_PATH,
            Charset.defaultCharset(), environmentString, false);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
            return;
        }

        error = FileUtils.moveRegularFile("termux.env.tmp", TremixConstants.TERMUX_ENV_TEMP_FILE_PATH, TremixConstants.TERMUX_ENV_FILE_PATH, true);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
        }
    }

    /** Get shell environment for Tremix. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext, boolean isFailSafe) {

        // Tremix environment builds upon the Android environment
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, isFailSafe);

        HashMap<String, String> termuxAppEnvironment = TremixAppShellEnvironment.getEnvironment(currentPackageContext);
        if (termuxAppEnvironment != null)
            environment.putAll(termuxAppEnvironment);

        HashMap<String, String> termuxApiAppEnvironment = TremixAPIShellEnvironment.getEnvironment(currentPackageContext);
        if (termuxApiAppEnvironment != null)
            environment.putAll(termuxApiAppEnvironment);

        environment.put(ENV_HOME, TremixConstants.TERMUX_HOME_DIR_PATH);
        environment.put(ENV_PREFIX, TremixConstants.TERMUX_PREFIX_DIR_PATH);

        // If failsafe is not enabled, then we keep default PATH and TMPDIR so that system binaries can be used
        if (!isFailSafe) {
            environment.put(ENV_TMPDIR, TremixConstants.TERMUX_TMP_PREFIX_DIR_PATH);
            if (TremixBootstrap.isAppPackageVariantAPTAndroid5()) {
                // Tremix in android 5/6 era shipped busybox binaries in applets directory
                environment.put(ENV_PATH, TremixConstants.TERMUX_BIN_PREFIX_DIR_PATH + ":" + TremixConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/applets");
                environment.put(ENV_LD_LIBRARY_PATH, TremixConstants.TERMUX_LIB_PREFIX_DIR_PATH);
            } else {
                // Tremix binaries on Android 7+ rely on DT_RUNPATH, so LD_LIBRARY_PATH should be unset by default
                environment.put(ENV_PATH, TremixConstants.TERMUX_BIN_PREFIX_DIR_PATH);
                environment.remove(ENV_LD_LIBRARY_PATH);
            }
        }

        return environment;
    }


    @NonNull
    @Override
    public String getDefaultWorkingDirectoryPath() {
        return TremixConstants.TERMUX_HOME_DIR_PATH;
    }

    @NonNull
    @Override
    public String getDefaultBinPath() {
        return TremixConstants.TERMUX_BIN_PREFIX_DIR_PATH;
    }

    @NonNull
    @Override
    public String[] setupShellCommandArguments(@NonNull String executable, String[] arguments) {
        return TremixShellUtils.setupShellCommandArguments(executable, arguments);
    }

}
