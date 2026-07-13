package com.termix.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termix.R;
import com.termix.shared.termux.settings.preferences.TremixFloatAppSharedPreferences;

@Keep
public class TremixFloatPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TremixFloatPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_float_preferences, rootKey);
    }

}

class TremixFloatPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TremixFloatAppSharedPreferences mPreferences;

    private static TremixFloatPreferencesDataStore mInstance;

    private TremixFloatPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TremixFloatAppSharedPreferences.build(context, true);
    }

    public static synchronized TremixFloatPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TremixFloatPreferencesDataStore(context);
        }
        return mInstance;
    }

}
