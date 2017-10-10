package com.aware.plugin.upmc.cancer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aware.Aware;

import java.util.Timer;
import java.util.TimerTask;

public class KeepAlive extends Service {

    Context context;

    public int counter=0;
    public KeepAlive(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
        context = applicationContext;
    }

    public KeepAlive() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Use startForeground to prevent Doze from kicking in.
        NotificationCompat.Builder b=new NotificationCompat.Builder(this, "Study");
        b.setOngoing(true)
                .setContentTitle("Study ongoing")
                .setContentText("?")
                .setTicker("?")
                .setSmallIcon(R.drawable.ic_action_aware_studies);
        startForeground(777, b.build());

        startTimer();


        Intent dialogIntent = new Intent(this, UPMC.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);

        //Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.cancer");

        // start plugin, keep this show going.
        //Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.cancer");

        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("com.aware.plugin.upmc.cancer.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //


    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}