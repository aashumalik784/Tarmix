package com.termix.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termix.R;
import com.termix.shared.termux.settings.preferences.TremixWidgetAppSharedPreferences;

@Keep
public class TremixWidgetPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TremixWidgetPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_widget_preferences, rootKey);
    }

}

class TremixWidgetPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TremixWidgetAppSharedPreferences mPreferences;

    private static TremixWidgetPreferencesDataStore mInstance;

    private TremixWidgetPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TremixWidgetAppSharedPreferences.build(context, true);
    }

    public static synchronized TremixWidgetPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TremixWidgetPreferencesDataStore(context);
        }
        return mInstance;
    }

}
