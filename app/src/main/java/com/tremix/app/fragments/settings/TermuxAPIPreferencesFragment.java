package com.termix.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termix.R;
import com.termix.shared.termux.settings.preferences.TremixAPIAppSharedPreferences;

@Keep
public class TremixAPIPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TremixAPIPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_api_preferences, rootKey);
    }

}

class TremixAPIPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TremixAPIAppSharedPreferences mPreferences;

    private static TremixAPIPreferencesDataStore mInstance;

    private TremixAPIPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TremixAPIAppSharedPreferences.build(context, true);
    }

    public static synchronized TremixAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TremixAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
