package com.example.multitimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    private ArrayList<Item> itemsList;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //gets all items with active alerts
            itemsList = SharedPreferencesHelper.getItemsWithActiveAlerts(context);

            for (int i = 0; i < itemsList.size(); i++) {
                Item item = itemsList.get(i);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent newIntent = new Intent(context, AlertReceiver.class);
                newIntent.putExtra("title", item.getmTitle());
                newIntent.putExtra("id", item.getmID());
                newIntent.setAction("ALERT");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, item.getmID(), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, item.getmMillisEnd(), pendingIntent);
            }
        }
    }
}