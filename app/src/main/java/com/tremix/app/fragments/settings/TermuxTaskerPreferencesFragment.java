package com.tremix.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tremix.R;
import com.tremix.shared.termux.settings.preferences.TremixTaskerAppSharedPreferences;

@Keep
public class TremixTaskerPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(TremixTaskerPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.termux_tasker_preferences, rootKey);
    }

}

class TremixTaskerPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TremixTaskerAppSharedPreferences mPreferences;

    private static TremixTaskerPreferencesDataStore mInstance;

    private TremixTaskerPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TremixTaskerAppSharedPreferences.build(context, true);
    }

    public static synchronized TremixTaskerPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TremixTaskerPreferencesDataStore(context);
        }
        return mInstance;
    }

}
