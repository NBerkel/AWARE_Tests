package com.aware.plugin.upmc.cancer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aware.Aware;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Activate/deactivate plugin
     */
    public static final String STATUS_PLUGIN_UPMC_CANCER = "status_plugin_upmc_cancer";

    private CheckBoxPreference status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_UPMC_CANCER);
        if (Aware.getSetting(this, STATUS_PLUGIN_UPMC_CANCER).length() == 0) {
            Aware.setSetting(this, STATUS_PLUGIN_UPMC_CANCER, true);
        }
        status.setChecked(Aware.getSetting(this, STATUS_PLUGIN_UPMC_CANCER).equals("true"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(STATUS_PLUGIN_UPMC_CANCER)) {
            if (sharedPreferences.getBoolean(key, false)) {
                Aware.setSetting(this, key, true);
                Aware.startPlugin(this, "com.aware.plugin.upmc.cancer");
            } else {
                Aware.setSetting(this, key, false);
                Aware.stopPlugin(this, "com.aware.plugin.upmc.cancer");
            }
        }
    }
}
