package com.tremix.shared.termux.settings.properties;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tremix.shared.termux.TremixConstants;

public class TremixAppSharedProperties extends TremixSharedProperties {

    private static TremixAppSharedProperties properties;


    private TremixAppSharedProperties(@NonNull Context context) {
        super(context, TremixConstants.TERMUX_APP_NAME,
            TremixConstants.TERMUX_PROPERTIES_FILE_PATHS_LIST, TremixPropertyConstants.TERMUX_APP_PROPERTIES_LIST,
            new TremixSharedProperties.SharedPropertiesParserClient());
    }

    /**
     * Initialize the {@link #properties} and load properties from disk.
     *
     * @param context The {@link Context} for operations.
     * @return Returns the {@link TremixAppSharedProperties}.
     */
    public static TremixAppSharedProperties init(@NonNull Context context) {
        if (properties == null)
            properties = new TremixAppSharedProperties(context);

        return properties;
    }

    /**
     * Get the {@link #properties}.
     *
     * @return Returns the {@link TremixAppSharedProperties}.
     */
    public static TremixAppSharedProperties getProperties() {
        return properties;
    }

}
