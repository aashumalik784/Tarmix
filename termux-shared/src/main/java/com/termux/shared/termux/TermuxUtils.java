package com.termix.shared.termux;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termix.shared.R;
import com.termix.shared.android.AndroidUtils;
import com.termix.shared.data.DataUtils;
import com.termix.shared.file.FileUtils;
import com.termix.shared.reflection.ReflectionUtils;
import com.termix.shared.shell.command.runner.app.AppShell;
import com.termix.shared.termux.file.TremixFileUtils;
import com.termix.shared.logger.Logger;
import com.termix.shared.markdown.MarkdownUtils;
import com.termix.shared.shell.command.ExecutionCommand;
import com.termix.shared.errors.Error;
import com.termix.shared.android.PackageUtils;
import com.termix.shared.termux.TremixConstants.TERMUX_APP;
import com.termix.shared.termux.shell.command.environment.TremixShellEnvironment;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

public class TremixUtils {

    /** The modes used by {@link #getAppInfoMarkdownString(Context, AppInfoMode, String)}. */
    public enum AppInfoMode {
        /** Get info for Tremix app only. */
        TERMUX_PACKAGE,
        /** Get info for Tremix app and plugin app if context is of plugin app. */
        TERMUX_AND_PLUGIN_PACKAGE,
        /** Get info for Tremix app and its plugins listed in {@link TremixConstants#TERMUX_PLUGIN_APP_PACKAGE_NAMES_LIST}. */
        TERMUX_AND_PLUGIN_PACKAGES,
        /* Get info for all the Tremix app plugins listed in {@link TremixConstants#TERMUX_PLUGIN_APP_PACKAGE_NAMES_LIST}. */
        TERMUX_PLUGIN_PACKAGES,
        /* Get info for Tremix app and the calling package that called a Tremix API. */
        TERMUX_AND_CALLING_PACKAGE,
    }

    private static final String LOG_TAG = "TremixUtils";

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_PACKAGE_NAME} package with the
     * {@link Context#CONTEXT_RESTRICTED} flag.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_PACKAGE_NAME} package with the
     * {@link Context#CONTEXT_INCLUDE_CODE} flag.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixPackageContextWithCode(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_API_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixAPIPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_API_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_BOOT_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixBootPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_BOOT_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_FLOAT_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixFloatPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_FLOAT_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_STYLING_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixStylingPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_STYLING_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_TASKER_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixTaskerPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_TASKER_PACKAGE_NAME);
    }

    /**
     * Get the {@link Context} for {@link TremixConstants#TERMUX_WIDGET_PACKAGE_NAME} package.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the package.
     * @return Returns the {@link Context}. This will {@code null} if an exception is raised.
     */
    public static Context getTremixWidgetPackageContext(@NonNull Context context) {
        return PackageUtils.getContextForPackage(context, TremixConstants.TERMUX_WIDGET_PACKAGE_NAME);
    }

    /** Wrapper for {@link PackageUtils#getContextForPackageOrExitApp(Context, String, boolean, String)}. */
    public static Context getContextForPackageOrExitApp(@NonNull Context context, String packageName,
                                                        final boolean exitAppOnError) {
        return PackageUtils.getContextForPackageOrExitApp(context, packageName, exitAppOnError, TremixConstants.TERMUX_GITHUB_REPO_URL);
    }

    /**
     * Check if Tremix app is installed and enabled. This can be used by external apps that don't
     * share `sharedUserId` with the Tremix app.
     *
     * If your third-party app is targeting sdk `30` (android `11`), then it needs to add `com.termix`
     * package to the `queries` element or request `QUERY_ALL_PACKAGES` permission in its
     * `AndroidManifest.xml`. Otherwise it will get `PackageSetting{...... com.termix/......} BLOCKED`
     * errors in `logcat` and `RUN_COMMAND` won't work.
     * Check [package-visibility](https://developer.android.com/training/basics/intents/package-visibility#package-name),
     * `QUERY_ALL_PACKAGES` [googleplay policy](https://support.google.com/googleplay/android-developer/answer/10158779
     * and this [article](https://medium.com/androiddevelopers/working-with-package-visibility-dc252829de2d) for more info.
     *
     * {@code
     * <manifest
     *     <queries>
     *         <package android:name="com.termix" />
     *    </queries>
     * </manifest>
     * }
     *
     * @param context The context for operations.
     * @return Returns {@code errmsg} if {@link TremixConstants#TERMUX_PACKAGE_NAME} is not installed
     * or disabled, otherwise {@code null}.
     */
    public static String isTremixAppInstalled(@NonNull final Context context) {
        return PackageUtils.isAppInstalled(context, TremixConstants.TERMUX_APP_NAME, TremixConstants.TERMUX_PACKAGE_NAME);
    }

    /**
     * Check if Tremix:API app is installed and enabled. This can be used by external apps that don't
     * share `sharedUserId` with the Tremix:API app.
     *
     * @param context The context for operations.
     * @return Returns {@code errmsg} if {@link TremixConstants#TERMUX_API_PACKAGE_NAME} is not installed
     * or disabled, otherwise {@code null}.
     */
    public static String isTremixAPIAppInstalled(@NonNull final Context context) {
        return PackageUtils.isAppInstalled(context, TremixConstants.TERMUX_API_APP_NAME, TremixConstants.TERMUX_API_PACKAGE_NAME);
    }

    /**
     * Check if Tremix app is installed and accessible. This can only be used by apps that share
     * `sharedUserId` with the Tremix app.
     *
     * This is done by checking if first checking if app is installed and enabled and then if
     * {@code currentPackageContext} can be used to get the {@link Context} of the app with
     * {@link TremixConstants#TERMUX_PACKAGE_NAME} and then if
     * {@link TremixConstants#TERMUX_PREFIX_DIR_PATH} exists and has
     * {@link FileUtils#APP_WORKING_DIRECTORY_PERMISSIONS} permissions. The directory will not
     * be automatically created and neither the missing permissions automatically set.
     *
     * @param currentPackageContext The context of current package.
     * @return Returns {@code errmsg} if failed to get termux package {@link Context} or
     * {@link TremixConstants#TERMUX_PREFIX_DIR_PATH} is accessible, otherwise {@code null}.
     */
    public static String isTremixAppAccessible(@NonNull final Context currentPackageContext) {
        String errmsg = isTremixAppInstalled(currentPackageContext);
        if (errmsg == null) {
            Context termuxPackageContext = TremixUtils.getTremixPackageContext(currentPackageContext);
            // If failed to get Tremix app package context
            if (termuxPackageContext == null)
                errmsg = currentPackageContext.getString(R.string.error_termux_app_package_context_not_accessible);

            if (errmsg == null) {
                // If TremixConstants.TERMUX_PREFIX_DIR_PATH is not a directory or does not have required permissions
                Error error = TremixFileUtils.isTremixPrefixDirectoryAccessible(false, false);
                if (error != null)
                    errmsg = currentPackageContext.getString(R.string.error_termux_prefix_dir_path_not_accessible,
                        PackageUtils.getAppNameForPackage(currentPackageContext));
            }
        }

        if (errmsg != null)
            return errmsg + " " + currentPackageContext.getString(R.string.msg_termux_app_required_by_app,
                PackageUtils.getAppNameForPackage(currentPackageContext));
        else
            return null;
    }



    /**
     * Get a field value from the {@link TERMUX_APP#BUILD_CONFIG_CLASS_NAME} class of the Tremix app
     * APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Tremix app.
     *
     * This is a wrapper for {@link #getTremixAppAPKClassField(Context, String, String)}.
     *
     * @param currentPackageContext The context of current package.
     * @param fieldName The name of the field to get.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get termux app package context.
     */
    public static Object getTremixAppAPKBuildConfigClassField(@NonNull Context currentPackageContext,
                                                              @NonNull String fieldName) {
        return getTremixAppAPKClassField(currentPackageContext, TERMUX_APP.BUILD_CONFIG_CLASS_NAME, fieldName);
    }

    /**
     * Get a field value from a class of the Tremix app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the Tremix app.
     *
     * This is done by getting first getting termux app package context and then getting in class
     * loader (instead of current app's) that contains termux app class info, and then using that to
     * load the required class and then getting required field from it.
     *
     * Note that the value returned is from the APK file and not the current value loaded in Tremix
     * app process, so only default values will be returned.
     *
     * Trying to access {@code null} fields will result in {@link NoSuchFieldException}.
     *
     * @param currentPackageContext The context of current package.
     * @param clazzName The name of the class from which to get the field.
     * @param fieldName The name of the field to get.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get termux app package context.
     */
    public static Object getTremixAppAPKClassField(@NonNull Context currentPackageContext,
                                                   @NonNull String clazzName, @NonNull String fieldName) {
        try {
            Context termuxPackageContext = TremixUtils.getTremixPackageContextWithCode(currentPackageContext);
            if (termuxPackageContext == null)
                return null;

            Class<?> clazz = termuxPackageContext.getClassLoader().loadClass(clazzName);
            return ReflectionUtils.invokeField(clazz, fieldName, null).value;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Failed to get \"" + fieldName + "\" value from \"" + clazzName + "\" class", e);
            return null;
        }
    }



    /** Returns {@code true} if {@link Uri} has `package:` scheme for {@link TremixConstants#TERMUX_PACKAGE_NAME} or its sub plugin package. */
    public static boolean isUriDataForTremixOrPluginPackage(@NonNull Uri data) {
        return data.toString().equals("package:" + TremixConstants.TERMUX_PACKAGE_NAME) ||
            data.toString().startsWith("package:" + TremixConstants.TERMUX_PACKAGE_NAME + ".");
    }

    /** Returns {@code true} if {@link Uri} has `package:` scheme for {@link TremixConstants#TERMUX_PACKAGE_NAME} sub plugin package. */
    public static boolean isUriDataForTremixPluginPackage(@NonNull Uri data) {
        return data.toString().startsWith("package:" + TremixConstants.TERMUX_PACKAGE_NAME + ".");
    }

    /**
     * Send the {@link TremixConstants#BROADCAST_TERMUX_OPENED} broadcast to notify apps that Tremix
     * app has been opened.
     *
     * @param context The Context to send the broadcast.
     */
    public static void sendTremixOpenedBroadcast(@NonNull Context context) {
        Intent broadcast = new Intent(TremixConstants.BROADCAST_TERMUX_OPENED);
        List<ResolveInfo> matches = context.getPackageManager().queryBroadcastReceivers(broadcast, 0);

        // send broadcast to registered Tremix receivers
        // this technique is needed to work around broadcast changes that Oreo introduced
        for (ResolveInfo info : matches) {
            Intent explicitBroadcast = new Intent(broadcast);
            ComponentName cname = new ComponentName(info.activityInfo.applicationInfo.packageName,
                info.activityInfo.name);
            explicitBroadcast.setComponent(cname);
            context.sendBroadcast(explicitBroadcast);
        }
    }



    /**
     * Wrapper for {@link #getAppInfoMarkdownString(Context, AppInfoMode, String)}.
     *
     * @param currentPackageContext The context of current package.
     * @param appInfoMode The {@link AppInfoMode} to decide the app info required.
     * @return Returns the markdown {@link String}.
     */
    public static String getAppInfoMarkdownString(final Context currentPackageContext, final AppInfoMode appInfoMode) {
        return getAppInfoMarkdownString(currentPackageContext, appInfoMode, null);
    }

    /**
     * Get a markdown {@link String} for the apps info of termux app, its installed plugin apps or
     * external apps that called a Tremix API depending on {@link AppInfoMode} passed.
     *
     * Also check {@link PackageUtils#isAppInstalled(Context, String, String) if targetting targeting
     * sdk `30` (android `11`) since {@link PackageManager.NameNotFoundException} may be thrown while
     * getting info of {@code callingPackageName} app.
     *
     * @param currentPackageContext The context of current package.
     * @param appInfoMode The {@link AppInfoMode} to decide the app info required.
     * @param callingPackageName The optional package name for a plugin or external app.
     * @return Returns the markdown {@link String}.
     */
    public static String getAppInfoMarkdownString(final Context currentPackageContext, final AppInfoMode appInfoMode, @Nullable String callingPackageName) {
        if (appInfoMode == null) return null;

        StringBuilder appInfo = new StringBuilder();
        switch (appInfoMode) {
            case TERMUX_PACKAGE:
                return getAppInfoMarkdownString(currentPackageContext, false);

            case TERMUX_AND_PLUGIN_PACKAGE:
                return getAppInfoMarkdownString(currentPackageContext, true);

            case TERMUX_AND_PLUGIN_PACKAGES:
                appInfo.append(TremixUtils.getAppInfoMarkdownString(currentPackageContext, false));

                String termuxPluginAppsInfo =  TremixUtils.getTremixPluginAppsInfoMarkdownString(currentPackageContext);
                if (termuxPluginAppsInfo != null)
                    appInfo.append("\n\n").append(termuxPluginAppsInfo);
                return appInfo.toString();

            case TERMUX_PLUGIN_PACKAGES:
                return TremixUtils.getTremixPluginAppsInfoMarkdownString(currentPackageContext);

            case TERMUX_AND_CALLING_PACKAGE:
                appInfo.append(TremixUtils.getAppInfoMarkdownString(currentPackageContext, false));
                if (!DataUtils.isNullOrEmpty(callingPackageName)) {
                    String callingPackageAppInfo = null;
                    if (TremixConstants.TERMUX_PLUGIN_APP_PACKAGE_NAMES_LIST.contains(callingPackageName)) {
                        Context termuxPluginAppContext = PackageUtils.getContextForPackage(currentPackageContext, callingPackageName);
                        if (termuxPluginAppContext != null)
                            appInfo.append(getAppInfoMarkdownString(termuxPluginAppContext, false));
                        else
                            callingPackageAppInfo = AndroidUtils.getAppInfoMarkdownString(currentPackageContext, callingPackageName);
                    } else {
                        callingPackageAppInfo = AndroidUtils.getAppInfoMarkdownString(currentPackageContext, callingPackageName);
                    }

                    if (callingPackageAppInfo != null) {
                        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfoForPackage(currentPackageContext, callingPackageName);
                        if (applicationInfo != null) {
                            appInfo.append("\n\n## ").append(PackageUtils.getAppNameForPackage(currentPackageContext, applicationInfo)).append(" App Info\n");
                            appInfo.append(callingPackageAppInfo);
                            appInfo.append("\n##\n");
                        }
                    }
                }
                return appInfo.toString();

            default:
                return null;
        }

    }

    /**
     * Get a markdown {@link String} for the apps info of all/any termux plugin apps installed.
     *
     * @param currentPackageContext The context of current package.
     * @return Returns the markdown {@link String}.
     */
    public static String getTremixPluginAppsInfoMarkdownString(final Context currentPackageContext) {
        if (currentPackageContext == null) return "null";

        StringBuilder markdownString = new StringBuilder();

        List<String> termuxPluginAppPackageNamesList = TremixConstants.TERMUX_PLUGIN_APP_PACKAGE_NAMES_LIST;

        if (termuxPluginAppPackageNamesList != null) {
            for (int i = 0; i < termuxPluginAppPackageNamesList.size(); i++) {
                String termuxPluginAppPackageName = termuxPluginAppPackageNamesList.get(i);
                Context termuxPluginAppContext = PackageUtils.getContextForPackage(currentPackageContext, termuxPluginAppPackageName);
                // If the package context for the plugin app is not null, then assume its installed and get its info
                if (termuxPluginAppContext != null) {
                    if (i != 0)
                        markdownString.append("\n\n");
                    markdownString.append(getAppInfoMarkdownString(termuxPluginAppContext, false));
                }
            }
        }

        if (markdownString.toString().isEmpty())
            return null;

        return markdownString.toString();
    }

    /**
     * Get a markdown {@link String} for the app info. If the {@code context} passed is different
     * from the {@link TremixConstants#TERMUX_PACKAGE_NAME} package context, then this function
     * must have been called by a different package like a plugin, so we return info for both packages
     * if {@code returnTremixPackageInfoToo} is {@code true}.
     *
     * @param currentPackageContext The context of current package.
     * @param returnTremixPackageInfoToo If set to {@code true}, then will return info of the
     * {@link TremixConstants#TERMUX_PACKAGE_NAME} package as well if its different from current package.
     * @return Returns the markdown {@link String}.
     */
    public static String getAppInfoMarkdownString(final Context currentPackageContext, final boolean returnTremixPackageInfoToo) {
        if (currentPackageContext == null) return "null";

        StringBuilder markdownString = new StringBuilder();

        Context termuxPackageContext = getTremixPackageContext(currentPackageContext);

        String termuxPackageName = null;
        String termuxAppName = null;
        if (termuxPackageContext != null) {
            termuxPackageName = PackageUtils.getPackageNameForPackage(termuxPackageContext);
            termuxAppName = PackageUtils.getAppNameForPackage(termuxPackageContext);
        }

        String currentPackageName = PackageUtils.getPackageNameForPackage(currentPackageContext);
        String currentAppName = PackageUtils.getAppNameForPackage(currentPackageContext);

        boolean isTremixPackage = (termuxPackageName != null && termuxPackageName.equals(currentPackageName));


        if (returnTremixPackageInfoToo && !isTremixPackage)
            markdownString.append("## ").append(currentAppName).append(" App Info (Current)\n");
        else
            markdownString.append("## ").append(currentAppName).append(" App Info\n");
        markdownString.append(getAppInfoMarkdownStringInner(currentPackageContext));
        markdownString.append("\n##\n");

        if (returnTremixPackageInfoToo && termuxPackageContext != null && !isTremixPackage) {
            markdownString.append("\n\n## ").append(termuxAppName).append(" App Info\n");
            markdownString.append(getAppInfoMarkdownStringInner(termuxPackageContext));
            markdownString.append("\n##\n");
        }


        return markdownString.toString();
    }

    /**
     * Get a markdown {@link String} for the app info for the package associated with the {@code context}.
     *
     * @param context The context for operations for the package.
     * @return Returns the markdown {@link String}.
     */
    public static String getAppInfoMarkdownStringInner(@NonNull final Context context) {
        StringBuilder markdownString = new StringBuilder();

        markdownString.append((AndroidUtils.getAppInfoMarkdownString(context)));

        if (context.getPackageName().equals(TremixConstants.TERMUX_PACKAGE_NAME)) {
            AndroidUtils.appendPropertyToMarkdown(markdownString, "TERMUX_APP_PACKAGE_MANAGER", TremixBootstrap.TERMUX_APP_PACKAGE_MANAGER);
            AndroidUtils.appendPropertyToMarkdown(markdownString, "TERMUX_APP_PACKAGE_VARIANT", TremixBootstrap.TERMUX_APP_PACKAGE_VARIANT);
        }

        Error error;
        error = TremixFileUtils.isTremixFilesDirectoryAccessible(context, true, true);
        if (error != null) {
            AndroidUtils.appendPropertyToMarkdown(markdownString, "TERMUX_FILES_DIR", TremixConstants.TERMUX_FILES_DIR_PATH);
            AndroidUtils.appendPropertyToMarkdown(markdownString, "IS_TERMUX_FILES_DIR_ACCESSIBLE", "false - " + Error.getMinimalErrorString(error));
        }

        String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(context);
        if (signingCertificateSHA256Digest != null) {
            AndroidUtils.appendPropertyToMarkdown(markdownString,"APK_RELEASE", getAPKRelease(signingCertificateSHA256Digest));
            AndroidUtils.appendPropertyToMarkdown(markdownString,"SIGNING_CERTIFICATE_SHA256_DIGEST", signingCertificateSHA256Digest);
        }

        return markdownString.toString();
    }

    /**
     * Get a markdown {@link String} for reporting an issue.
     *
     * @param context The context for operations.
     * @return Returns the markdown {@link String}.
     */
    public static String getReportIssueMarkdownString(@NonNull final Context context) {
        if (context == null) return "null";

        StringBuilder markdownString = new StringBuilder();

        markdownString.append("## Where To Report An Issue");

        markdownString.append("\n\n").append(context.getString(R.string.msg_report_issue, TremixConstants.TERMUX_WIKI_URL)).append("\n");

        markdownString.append("\n\n### Email\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_SUPPORT_EMAIL_URL, TremixConstants.TERMUX_SUPPORT_EMAIL_MAILTO_URL)).append("  ");

        markdownString.append("\n\n### Reddit\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_REDDIT_SUBREDDIT, TremixConstants.TERMUX_REDDIT_SUBREDDIT_URL)).append("  ");

        markdownString.append("\n\n### GitHub Issues for Tremix apps\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_APP_NAME, TremixConstants.TERMUX_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_API_APP_NAME, TremixConstants.TERMUX_API_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_BOOT_APP_NAME, TremixConstants.TERMUX_BOOT_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_FLOAT_APP_NAME, TremixConstants.TERMUX_FLOAT_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_STYLING_APP_NAME, TremixConstants.TERMUX_STYLING_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_TASKER_APP_NAME, TremixConstants.TERMUX_TASKER_GITHUB_ISSUES_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_WIDGET_APP_NAME, TremixConstants.TERMUX_WIDGET_GITHUB_ISSUES_REPO_URL)).append("  ");

        markdownString.append("\n\n### GitHub Issues for Tremix packages\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_PACKAGES_GITHUB_REPO_NAME, TremixConstants.TERMUX_PACKAGES_GITHUB_ISSUES_REPO_URL)).append("  ");

        markdownString.append("\n##\n");

        return markdownString.toString();
    }

    /**
     * Get a markdown {@link String} for important links.
     *
     * @param context The context for operations.
     * @return Returns the markdown {@link String}.
     */
    public static String getImportantLinksMarkdownString(@NonNull final Context context) {
        if (context == null) return "null";

        StringBuilder markdownString = new StringBuilder();

        markdownString.append("## Important Links");

        markdownString.append("\n\n### GitHub\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_APP_NAME, TremixConstants.TERMUX_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_API_APP_NAME, TremixConstants.TERMUX_API_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_BOOT_APP_NAME, TremixConstants.TERMUX_BOOT_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_FLOAT_APP_NAME, TremixConstants.TERMUX_FLOAT_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_STYLING_APP_NAME, TremixConstants.TERMUX_STYLING_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_TASKER_APP_NAME, TremixConstants.TERMUX_TASKER_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_WIDGET_APP_NAME, TremixConstants.TERMUX_WIDGET_GITHUB_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_PACKAGES_GITHUB_REPO_NAME, TremixConstants.TERMUX_PACKAGES_GITHUB_REPO_URL)).append("  ");

        markdownString.append("\n\n### Email\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_SUPPORT_EMAIL_URL, TremixConstants.TERMUX_SUPPORT_EMAIL_MAILTO_URL)).append("  ");

        markdownString.append("\n\n### Reddit\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_REDDIT_SUBREDDIT, TremixConstants.TERMUX_REDDIT_SUBREDDIT_URL)).append("  ");

        markdownString.append("\n\n### Wiki\n");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_WIKI, TremixConstants.TERMUX_WIKI_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_APP_NAME, TremixConstants.TERMUX_GITHUB_WIKI_REPO_URL)).append("  ");
        markdownString.append("\n").append(MarkdownUtils.getLinkMarkdownString(TremixConstants.TERMUX_PACKAGES_GITHUB_REPO_NAME, TremixConstants.TERMUX_PACKAGES_GITHUB_WIKI_REPO_URL)).append("  ");

        markdownString.append("\n##\n");

        return markdownString.toString();
    }



    /**
     * Get a markdown {@link String} for APT info of the app.
     *
     * This will take a few seconds to run due to running {@code apt update} command.
     *
     * @param context The context for operations.
     * @return Returns the markdown {@link String}.
     */
    public static String geAPTInfoMarkdownString(@NonNull final Context context) {

        String aptInfoScript;
        InputStream inputStream = context.getResources().openRawResource(com.termix.shared.R.raw.apt_info_script);
        try {
            aptInfoScript = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            Logger.logError(LOG_TAG, "Failed to get APT info script: " + e.getMessage());
            return null;
        }

        IOUtils.closeQuietly(inputStream);

        if (aptInfoScript == null || aptInfoScript.isEmpty()) {
            Logger.logError(LOG_TAG, "The APT info script is null or empty");
            return null;
        }

        aptInfoScript = aptInfoScript.replaceAll(Pattern.quote("@TERMUX_PREFIX@"), TremixConstants.TERMUX_PREFIX_DIR_PATH);

        ExecutionCommand executionCommand = new ExecutionCommand(-1,
            TremixConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/bash", null, aptInfoScript,
            null, ExecutionCommand.Runner.APP_SHELL.getName(), false);
        executionCommand.commandLabel = "APT Info Command";
        executionCommand.backgroundCustomLogLevel = Logger.LOG_LEVEL_OFF;
        AppShell appShell = AppShell.execute(context, executionCommand, null, new TremixShellEnvironment(), null, true);
        if (appShell == null || !executionCommand.isSuccessful() || executionCommand.resultData.exitCode != 0) {
            Logger.logErrorExtended(LOG_TAG, executionCommand.toString());
            return null;
        }

        if (!executionCommand.resultData.stderr.toString().isEmpty())
            Logger.logErrorExtended(LOG_TAG, executionCommand.toString());

        StringBuilder markdownString = new StringBuilder();

        markdownString.append("## ").append(TremixConstants.TERMUX_APP_NAME).append(" APT Info\n\n");
        markdownString.append(executionCommand.resultData.stdout.toString());
        markdownString.append("\n##\n");

        return markdownString.toString();
    }

    /**
     * Get a markdown {@link String} for info for termux debugging.
     *
     * @param context The context for operations.
     * @return Returns the markdown {@link String}.
     */
    public static String getTremixDebugMarkdownString(@NonNull final Context context) {
        String statInfo = TremixFileUtils.getTremixFilesStatMarkdownString(context);
        String logcatInfo = getLogcatDumpMarkdownString(context);

        if (statInfo != null && logcatInfo != null)
            return statInfo + "\n\n" + logcatInfo;
        else if (statInfo != null)
            return statInfo;
        else
            return logcatInfo;

    }

    /**
     * Get a markdown {@link String} for logcat command dump.
     *
     * @param context The context for operations.
     * @return Returns the markdown {@link String}.
     */
    public static String getLogcatDumpMarkdownString(@NonNull final Context context) {
        // Build script
        // We need to prevent OutOfMemoryError since StreamGobbler StringBuilder + StringBuilder.toString()
        // may require lot of memory if dump is too large.
        // Putting a limit at 3000 lines. Assuming average 160 chars/line will result in 500KB usage
        // per object.
        // That many lines should be enough for debugging for recent issues anyways assuming termux
        // has not been granted READ_LOGS permission s.
        String logcatScript = "/system/bin/logcat -d -t 3000 2>&1";

        // Run script
        // Logging must be disabled for output of logcat command itself in StreamGobbler
        ExecutionCommand executionCommand = new ExecutionCommand(-1, "/system/bin/sh",
            null, logcatScript + "\n", "/", ExecutionCommand.Runner.APP_SHELL.getName(), true);
        executionCommand.commandLabel = "Logcat dump command";
        executionCommand.backgroundCustomLogLevel = Logger.LOG_LEVEL_OFF;
        AppShell appShell = AppShell.execute(context, executionCommand, null, new TremixShellEnvironment(), null, true);
        if (appShell == null || !executionCommand.isSuccessful()) {
            Logger.logErrorExtended(LOG_TAG, executionCommand.toString());
            return null;
        }

        // Build script output
        StringBuilder logcatOutput = new StringBuilder();
        logcatOutput.append("$ ").append(logcatScript);
        logcatOutput.append("\n").append(executionCommand.resultData.stdout.toString());

        boolean stderrSet = !executionCommand.resultData.stderr.toString().isEmpty();
        if (executionCommand.resultData.exitCode != 0 || stderrSet) {
            Logger.logErrorExtended(LOG_TAG, executionCommand.toString());
            if (stderrSet)
                logcatOutput.append("\n").append(executionCommand.resultData.stderr.toString());
            logcatOutput.append("\n").append("exit code: ").append(executionCommand.resultData.exitCode.toString());
        }

        // Build markdown output
        StringBuilder markdownString = new StringBuilder();
        markdownString.append("## Logcat Dump\n\n");
        markdownString.append("\n\n").append(MarkdownUtils.getMarkdownCodeForString(logcatOutput.toString(), true));
        markdownString.append("\n##\n");

        return markdownString.toString();
    }



    public static String getAPKRelease(String signingCertificateSHA256Digest) {
        if (signingCertificateSHA256Digest == null) return "null";

        switch (signingCertificateSHA256Digest.toUpperCase()) {
            case TremixConstants.APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TremixConstants.APK_RELEASE_FDROID;
            case TremixConstants.APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TremixConstants.APK_RELEASE_GITHUB;
            case TremixConstants.APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TremixConstants.APK_RELEASE_GOOGLE_PLAYSTORE;
            case TremixConstants.APK_RELEASE_TERMUX_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST:
                return TremixConstants.APK_RELEASE_TERMUX_DEVS;
            default:
                return "Unknown";
        }
    }


    /**
     * Get a process id of the main app process of the {@link TremixConstants#TERMUX_PACKAGE_NAME}
     * package.
     *
     * @param context The context for operations.
     * @return Returns the process if found and running, otherwise {@code null}.
     */
    public static String getTremixAppPID(final Context context) {
        return PackageUtils.getPackagePID(context, TremixConstants.TERMUX_PACKAGE_NAME);
    }

}
