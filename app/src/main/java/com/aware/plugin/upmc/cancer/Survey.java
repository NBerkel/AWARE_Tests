package com.aware.plugin.upmc.cancer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import static android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS;
import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

/**
 * Created by nielsv on 09-26-2017.
 */

public class Survey extends IntentService {
    public Survey() {
        super("Survey service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();


        Log.d("Niels debug", "Survey onHandleIntent called");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon( R.drawable.ic_stat_survey );
        mBuilder.setContentTitle( "UPMC Questionnaire" );
        mBuilder.setContentText( "Tap to answer." );
        mBuilder.setDefaults( Notification.DEFAULT_ALL );
        mBuilder.setPriority(PRIORITY_MAX);
        mBuilder.setVisibility(VISIBILITY_PUBLIC);
        mBuilder.setOnlyAlertOnce( true );
        mBuilder.setAutoCancel( true );
        //todo
        //mBuilder.setDeleteIntent(createDeleteIntent(context));


        Intent survey = new Intent(getApplicationContext(), Q_PANAS.class);
        survey.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        PendingIntent onclick = PendingIntent.getActivity(getApplicationContext(), 0, survey, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(onclick);

        NotificationManager notManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.notify(600, mBuilder.build());

        // Save notification to database
        ContentValues vals = new ContentValues();
        vals.put(Provider.Notification_Data.STATUS, "New");
        vals.put(Provider.Notification_Data.TIMESTAMP, System.currentTimeMillis());
        vals.put(Provider.Notification_Data.FIRED, System.currentTimeMillis());
        vals.put(Provider.Notification_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
        getContentResolver().insert(Provider.Notification_Data.CONTENT_URI, vals);

    }
}