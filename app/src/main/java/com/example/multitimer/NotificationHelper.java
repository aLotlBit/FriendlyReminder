package com.example.multitimer;

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

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String CHANNEL_ID = "1";
    public static final String CHANNEL_NAME = "Channel";

    public static final String ACTION_RESTART = "ActionRestart";

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

    //TODO only for API 16+
    public void createChannel() {

       // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        NotificationChannel mChannel =
                new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.enableLights(true);
        mChannel.enableVibration(true);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        //mChannel.setSound(soundUri, audioAttributes);
        getManager().createNotificationChannel(mChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;

    }


    public NotificationCompat.Builder getChannelNotification(String title, String message, Integer ID) {

        Intent resetIntent = new Intent(this, AlertReceiver.class);
        resetIntent.setAction(ACTION_RESTART);
        resetIntent.putExtra("restart_reminder", "restart_reminder");
        resetIntent.putExtra("id", ID);
        resetIntent.putExtra("title", title);

        PendingIntent resetPendingIntent =
                PendingIntent.getBroadcast(this, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_stat_name)
            //.setSound(soundUri);
            .setContentIntent(resetPendingIntent)
            .addAction(R.drawable.more, "Restart", resetPendingIntent);

    }
}
