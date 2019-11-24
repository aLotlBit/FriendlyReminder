package com.example.multitimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TimerView extends AppCompatActivity {

    private Button mButtonStartPause;

    private String label;
    private String timeLeftInMillis;

    private TextView timerViewLabel;

    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);
        timerViewLabel = findViewById(R.id.timerViewLabel);

        Intent myIntent = getIntent();
        label = myIntent.getStringExtra("label");
        timeLeftInMillis = myIntent.getStringExtra("duration");
        timerViewLabel.setText(label);

        mButtonStartPause = findViewById(R.id.btn_start_pause);

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimer();
            }
        });

    }


    private void setTimer() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
      //  timerViewLabel.setText("Set");
        Log.i(TAG, "Alert Set ");

    }


}