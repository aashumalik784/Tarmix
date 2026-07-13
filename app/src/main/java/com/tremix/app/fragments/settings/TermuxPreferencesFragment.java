package com.tremix.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tremix.R;
import com.tremix.shared.termux.settings.preferences.TremixAppSharedPreferences;

@Keep
public class TremixPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TremixPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_preferences, rootKey);
    }

}

class TremixPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TremixAppSharedPreferences mPreferences;

    private static TremixPreferencesDataStore mInstance;

    private TremixPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TremixAppSharedPreferences.build(context, true);
    }

    public static synchronized TremixPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TremixPreferencesDataStore(context);
        }
        return mInstance;
    }

}
