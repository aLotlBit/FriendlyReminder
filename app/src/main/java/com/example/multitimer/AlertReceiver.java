package com.example.multitimer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = "MyActivity";

    //TODO useless?
    private Context mContext;


    //   public Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        //TODO intentFilter with ACTION_RESTART
        if(intent.hasExtra("restart_reminder")){

            int id = intent.getIntExtra("id", -1);
            String title = intent.getStringExtra("title");

            int interval = SharedPreferencesHelper.getIntervalForReset(context, id);
            long millis_end = System.currentTimeMillis() + Item.daysToMillis(interval);
            SharedPreferencesHelper.setLong(context, "millis_end_" + id, millis_end);
            SharedPreferencesHelper.setLong(context, "millis_start_" + id, System.currentTimeMillis());
            SharedPreferencesHelper.setInt(context, "alert_active_" + id, 1);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, AlertReceiver.class);
            newIntent.putExtra("title", title);
            newIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis_end, pendingIntent);

            /*
            //opens App
            Intent launch_intent = new  Intent(context, MainActivity.class);
            launch_intent.setComponent(new ComponentName("com.example.multitimer","com.example.multitimer.MainActivity"));
            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launch_intent.putExtra("RESTART_REMINDER", "RESTART_REMINDER");
            launch_intent.putExtra("id", ID);
            launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(launch_intent);

             */

            //TODO check if activity is running
            //resets when app is open
            Intent reset_intent = new Intent("RESTART_REMINDER");
            reset_intent.putExtra("id", id);
            context.sendBroadcast(reset_intent);

            //cancel notification when restart button is clicked
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);

            //NotificationManagerCompat.from(context).cancel(String.valueOf(id), 0);

            //supposed to close notifications
            //Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            //context.sendBroadcast(it);

        } else {
            String title = intent.getStringExtra("title");
            int id = intent.getIntExtra("id", 0);
            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, "Yiss!!!", id);
            notificationHelper.getManager().notify(id, nb.build());
            //disable alertActive for item after alert in sharedPrefs
            SharedPreferencesHelper.setInt(context, "alert_active_" + id, 0);
            //intent to disable alertActive in Item when app is open
            Intent reset_intent = new Intent("RESET_ALERT_ACTIVE");
            reset_intent.putExtra("id", id);
            context.sendBroadcast(reset_intent);
        }
    }
}
