package com.example.duck_automation_game.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
//            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
//            float  batteryPercentage = ((float)level/(float)scale) * 100; doesnt work lamao

            Toast.makeText(context, "battery is low, please be advised  to turn on Power Saving Mode", Toast.LENGTH_LONG).show();

        }


    }
}