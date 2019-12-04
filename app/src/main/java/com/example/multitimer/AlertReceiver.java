package com.example.multitimer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {

    private static final String TAG = "MyActivity";

    private Context mContext;


    //   public Context context;
    @Override
    public void onReceive(Context context, Intent intent) {



        //String action = intent.getExtras().getString("restart");
        if(intent.hasExtra("restart_reminder")){

            int ID = intent.getIntExtra("id", -1);

            //resets when is closed
            Intent launch_intent = new  Intent(context, MainActivity.class);
            launch_intent.setComponent(new ComponentName("com.example.multitimer","com.example.multitimer.MainActivity"));
            launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            launch_intent.putExtra("RESTART_REMINDER", "RESTART_REMINDER");
            launch_intent.putExtra("id", ID);
            launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(launch_intent);

            //resets when app is open
            Intent reset_intent = new Intent("RESTART_REMINDER");
            reset_intent.putExtra("id", ID);
            context.sendBroadcast(reset_intent);


            //supposed to close notifications
            //Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            //context.sendBroadcast(it);

        } else {

            String title = intent.getStringExtra("title");
            int id = intent.getIntExtra("id", 0);

            NotificationHelper notificationHelper = new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, "Yiss!!!", id);
            notificationHelper.getManager().notify(id, nb.build());
        }



    }


    public void restartAlert(){


    }


}
