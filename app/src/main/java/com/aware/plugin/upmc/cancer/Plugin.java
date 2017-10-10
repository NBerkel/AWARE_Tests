package com.aware.plugin.upmc.cancer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.providers.Scheduler_Provider;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Radio;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Scheduler;

import org.json.JSONException;

public class Plugin extends Aware_Plugin {

    public static String ACTION_CANCER_EMOTION = "ACTION_CANCER_EMOTION";

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "UPMC Cancer";

    }

    public static class SurveyListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Niels debug", "Action surveylistener broadcastreceiver!");

            if (intent.getAction().equals(ACTION_CANCER_EMOTION)) {
                Log.d("Niels debug", "Action emotion intent received!");

                Intent surveyService = new Intent(context, Survey.class);
                context.startService(surveyService);
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {
            Aware.setSetting(this, Settings.STATUS_PLUGIN_UPMC_CANCER, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 6);

            // if intent != null
            if (intent != null && intent.getExtras() != null && intent.getBooleanExtra("schedule", false)) {
                Cursor randoms = getContentResolver().query(Scheduler_Provider.Scheduler_Data.CONTENT_URI, null,
                        Scheduler_Provider.Scheduler_Data.SCHEDULE_ID + " LIKE 'cognition_random_%'", null, null);

                if (randoms != null && randoms.getCount() > 0) {
                    //already scheduled randoms, do nothing.
                    Log.d(TAG, "Randoms already scheduled, do nothing.");
                    randoms.close();
                } else {
                    try {
                        Scheduler.Schedule schedule = Scheduler.getSchedule(getApplicationContext(), "cognition");
                        if (schedule == null) {
                            schedule = new Scheduler.Schedule("cognition");
                            schedule.addHour(9);
                            schedule.addHour(21);

                            Log.d("Niels debug", "Scheduling randoms");

                            // TODO: plan according to scheme (random order)
                            schedule.random(10, 10); // daily amount, minimum interval
                            schedule.setActionType(Scheduler.ACTION_TYPE_BROADCAST)
                                    .setActionIntentAction(Plugin.ACTION_CANCER_EMOTION);
                            Scheduler.saveSchedule(this, schedule);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            Aware.startAWARE(this);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aware.setSetting(this, Settings.STATUS_PLUGIN_UPMC_CANCER, false);
        Aware.stopAWARE(this);
    }
}
