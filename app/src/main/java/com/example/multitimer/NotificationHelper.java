package com.example.multitimer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.lang.annotation.Target;

public class NotificationHelper extends ContextWrapper {
    public static final String CHANNEL_ID = "1";
    public static final String CHANNEL_NAME = "CHANNEL_1";

    public static final String ACTION_RESTART = "RESTART";

    private NotificationManager mManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);



    private Uri soundUri;

    {
        soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getApplicationContext().getPackageName() + "/" + R.raw.alarm);
    }



    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    // Notification Channel is only created on API level 26 and above
    @RequiresApi(26)
    public void createChannel() {

       // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationChannel mChannel =
                new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        /*
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        mChannel.setSound(soundUri, audioAttributes);
         */

        getManager().createNotificationChannel(mChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }


    public NotificationCompat.Builder getChannelNotification(String title, String message, Integer ID) {

        Intent contentIntent = new Intent(this, AlertReceiver.class);
        contentIntent.setAction("CONTENT");
        contentIntent.putExtra("id", ID);
        contentIntent.putExtra("title", title);

        PendingIntent contentPending =
                PendingIntent.getBroadcast(this, ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent resetIntent = new Intent(this, AlertReceiver.class);
        resetIntent.setAction(ACTION_RESTART);
        resetIntent.putExtra("id", ID);
        resetIntent.putExtra("title", title);

        PendingIntent resetPendingIntent =
                PendingIntent.getBroadcast(this, ID, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent plusOneDayIntent = new Intent(this, AlertReceiver.class);
        plusOneDayIntent.setAction("PLUS_ONE_DAY");
        plusOneDayIntent.putExtra("id", ID);
        plusOneDayIntent.putExtra("title", title);

        PendingIntent plusOneDayPending =
                PendingIntent.getBroadcast(this, ID, plusOneDayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent plus3hIntent = new Intent(this, AlertReceiver.class);
        plus3hIntent.setAction("PLUS_3H");
        plus3hIntent.putExtra("id", ID);
        plus3hIntent.putExtra("title", title);

        PendingIntent plus3hPending =
                PendingIntent.getBroadcast(this, ID, plus3hIntent, PendingIntent.FLAG_UPDATE_CURRENT);

         NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setContentTitle(title);
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.ic_stat_name);
            //.setSound(soundUri);
            builder.setContentIntent(contentPending);

            builder.addAction(R.drawable.more, getString(R.string.btn_plus_3h), plus3hPending);
            builder.addAction(R.drawable.less, getString(R.string.btn_plus_one_day), plusOneDayPending);
            builder.addAction(R.drawable.more, getString(R.string.btn_restart), resetPendingIntent);

         return builder;
    }
}
