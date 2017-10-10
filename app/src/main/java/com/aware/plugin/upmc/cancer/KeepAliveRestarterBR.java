package com.aware.plugin.upmc.cancer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeepAliveRestarterBR extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(KeepAliveRestarterBR.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, KeepAlive.class));;
    }
}
