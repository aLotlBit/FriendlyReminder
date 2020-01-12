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

    private static final String TAG = "ALERT_RECEIVER";

    //TODO useless?
    //private Context mContext;

    //TODO change to actual values
    static private long ONE_DAY = 86400000L;
    static private long THREE_HOURS = 10800000L;


    //   public Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case "ALERT":
                    String title = intent.getStringExtra("title");
                    int id = intent.getIntExtra("id", 0);
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, "", id);
                    notificationHelper.getManager().notify(id, nb.build());
                    //set AlertStatus for item to "alert ringing and not marked seen (4)" after alert in sharedPrefs
                    SharedPreferencesHelper.setInt(context, "alert_status_" + id, 4);
                    //intent to set alertStaus in Item when app is open
                    Intent reset_intent = new Intent("DISABLE_ALERT_ACTIVE");
                    reset_intent.putExtra("id", id);
                    context.sendBroadcast(reset_intent);
                    Log.d(TAG, "AlertIntent Received  :" + intent.getAction());
                    break;

                case "RESTART":
                    int id_1 = intent.getIntExtra("id", -1);
                    String title_1 = intent.getStringExtra("title");

                    int interval = SharedPreferencesHelper.getIntervalForReset(context, id_1);
                    long millis_end_old = SharedPreferencesHelper.getMillisEnd(context, id_1);

                    long millis_end = millis_end_old + Item.daysToMillis(interval);
                    SharedPreferencesHelper.setLong(context, "millis_end_" + id_1, millis_end);
                    SharedPreferencesHelper.setLong(context, "millis_start_" + id_1, System.currentTimeMillis());
                    SharedPreferencesHelper.setInt(context, "alert_status_" + id_1, 3);
                    setAlert(context, id_1, title_1, millis_end);

                    //resets values in item when app is open
                    sendIntentToMain(context, "RESTART_RUNNING", id_1);
                    //cancel notification when restart button is clicked
                    cancelNotification(context, id_1);
                    Log.d(TAG, "RestartIntent Received");
                    break;

                case "PLUS_ONE_DAY":
                    Log.d(TAG, "PlusOneDay received");

                    int id_2 = intent.getIntExtra("id", -1);
                    String title_2 = intent.getStringExtra("title");
                    long millis_end_2 = SharedPreferencesHelper.getMillisEnd(context, id_2) + ONE_DAY;

                    SharedPreferencesHelper.setLong(context, "millis_end_" + id_2, millis_end_2);
                    SharedPreferencesHelper.setInt(context, "alert_status_" + id_2, 3);

                    setAlert(context, id_2, title_2, millis_end_2);
                    sendIntentToMain(context, "PLUS_ONE_DAY", id_2);
                    cancelNotification(context, id_2);
                    break;

                case "PLUS_3H":
                    Log.d(TAG, "Plus3H received");
                    int id_3 = intent.getIntExtra("id", -1);
                    String title_3 = intent.getStringExtra("title");
                    long millis_end_3 = SharedPreferencesHelper.getMillisEnd(context, id_3) + THREE_HOURS;

                    SharedPreferencesHelper.setLong(context, "millis_end_" + id_3, millis_end_3);
                    SharedPreferencesHelper.setInt(context, "alert_status_" + id_3, 3);

                    setAlert(context, id_3, title_3, millis_end_3);
                    sendIntentToMain(context, "PLUS_THREE_HOURS", id_3);
                    cancelNotification(context, id_3);
                    break;

                case "CONTENT":
                    Log.d(TAG, "Content received");

                    int id_4 = intent.getIntExtra("id", -1);

                    //launches app and expands item
                    Intent launch_intent = new  Intent(context, MainActivity.class);
                    launch_intent.setComponent(new ComponentName("com.example.multitimer","com.example.multitimer.MainActivity"));
                    launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    launch_intent.putExtra("id", id_4);
                    launch_intent.setAction("CONTENT");
                    launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(launch_intent);

                    //expands item when app is not closed
                    sendIntentToMain(context, "CONTENT", id_4);
                    cancelNotification(context, id_4);
                    break;
            }
    }

    private void setAlert(Context context, Integer id, String title, long millis ) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        intent.setAction("ALERT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }

    private void sendIntentToMain(Context context, String action, int id){
        Intent intent = new Intent(action);
        intent.putExtra("id", id);
        context.sendBroadcast(intent);
    }

    private void cancelNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}

