package com.tremix.shared.termux.shell.command.environment;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tremix.shared.android.PackageUtils;
import com.tremix.shared.shell.command.environment.ShellEnvironmentUtils;
import com.tremix.shared.termux.TremixConstants;
import com.tremix.shared.termux.TremixUtils;

import java.util.HashMap;

/**
 * Environment for {@link TremixConstants#TERMUX_API_PACKAGE_NAME} app.
 */
public class TremixAPIShellEnvironment {

    /** Environment variable prefix for the Tremix:API app. */
    public static final String TERMUX_API_APP_ENV_PREFIX = TremixConstants.TERMUX_ENV_PREFIX_ROOT + "_API_APP__";

    /** Environment variable for the Tremix:API app version. */
    public static final String ENV_TERMUX_API_APP__VERSION_NAME = TERMUX_API_APP_ENV_PREFIX + "VERSION_NAME";

    /** Get shell environment for Tremix:API app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        if (TremixUtils.isTremixAPIAppInstalled(currentPackageContext) != null) return null;

        String packageName = TremixConstants.TERMUX_API_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return null;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_TERMUX_API_APP__VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));

        return environment;
    }

}
