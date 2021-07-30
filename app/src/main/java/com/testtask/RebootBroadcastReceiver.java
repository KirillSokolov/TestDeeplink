package com.testtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.SyncService;

class RebootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
        Log.i("Autostart", "started");
    }
}
