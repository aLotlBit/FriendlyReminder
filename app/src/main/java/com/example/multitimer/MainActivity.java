package com.example.multitimer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

//    TinyDB tinydb = new TinyDB(getApplicationContext());

 //   SharedPreferences sharedprefs;


    private ArrayList<Item> itemsList;

    private ItemAdapter adapter;

    private Boolean newItem;

    EditText enterTitle;

    ListView listView;

    private NotificationHelper notificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);

        itemsList = new ArrayList<Item>();
        adapter = new ItemAdapter(this, itemsList);
        newItem = false;

        loadFromSharedPrefs(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        listView.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNew();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Item item = (Item) listView.getItemAtPosition(position);

                //Log.i("Position", valueOf(position));
                showDialogItem(item, position);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToSharedPrefs();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private boolean saveToSharedPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();

        mEdit1.putInt("items_list_size", itemsList.size());

        for (int i=0;i<itemsList.size();i++)
        {
            String title = itemsList.get(i).getmTitle();
            mEdit1.putString("Title_" + i, title);
            long millisStart = itemsList.get(i).getmMillisStart();
            mEdit1.putLong("MillisStart_" + i, millisStart);
        }

        return mEdit1.commit();
    }


    public void loadFromSharedPrefs(Context mContext)
    {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit2 = mSharedPreference1.edit();

        itemsList.clear();
        int size = mSharedPreference1.getInt("items_list_size", 0);

        for(int i=0;i<size;i++)
        {
            String title = mSharedPreference1.getString("Title_" + i, null);
            long millisStart = mSharedPreference1.getLong("MillisStart_" + i, 0);
            itemsList.add(new Item(title, millisStart, 0, 0, 0, 0) );
            mEdit2.remove("Title_" + i);
            mEdit2.remove("MillisStart_" + i);
            mEdit2.commit();

        }

    }


    private void showDialogNew() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_new, null);
        final EditText enterTitle = dialogView.findViewById(R.id.edit_text_title);

        enterTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                enterTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager= (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(enterTitle, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        enterTitle.requestFocus();


        builder.setView(dialogView);
        builder.setView(dialogView);

        builder.setMessage("Enter Title");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //needed so dialog can stay open when positve button is pressed
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String title = enterTitle.getText().toString();
                if (title.length() < 1 ) {
                    Toast.makeText(getApplicationContext(), "Enter Title!!!", Toast.LENGTH_SHORT).show();

                } else {
                    showDialogItem(new Item(title, System.currentTimeMillis(), 0, 0, 0, 0),-1);
                    newItem = true;

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }

            }
        });
    }

    private void showDialogItem(final Item item, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_reminder, null);
        builder.setView(dialogView);

        final String title = item.getmTitle();
        String daysPassed = item.getDaysPassed();

        TextView tvDaysPassed = (TextView) dialogView.findViewById(R.id.dialog_days_since);
        tvDaysPassed.setText(daysPassed);

        Button btnSetInterval = (Button) dialogView.findViewById(R.id.btn_set_interval);
        Button btnSetTimeOfDay = (Button) dialogView.findViewById(R.id.btn_set_time_of_day);
        TextView tvDaysLeft = (TextView) dialogView.findViewById(R.id.tv_days_left);

        btnSetInterval.setText(String.valueOf(item.getmInterval()));
        btnSetTimeOfDay.setText(String.valueOf(item.getmHourOfDay()) + " : " + String.valueOf(item.getmMinuteOfDay()));
        tvDaysLeft.setText(String.valueOf(item.getDaysLeft()));


        builder.setMessage(title);
        //adapter.notifyDataSetChanged();



        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (newItem) {
                    itemsList.add(item);
                    adapter.notifyDataSetChanged();
                    newItem = false;
                } else {

                    long millisEnd =  item.getmMillisEnd();
                    setNotification(millisEnd, item.getmTitle(), position);

                    adapter.notifyDataSetChanged();

                    /*
                    long now = System.currentTimeMillis();
                    Calendar calendar = Calendar.getInstance();
                    long later =  now + 86400000L;
                    //calendar.setLenient(true);
                    calendar.setTimeInMillis(later);
                    calendar.set(Calendar.HOUR_OF_DAY, 2);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    String time = calendar.getTime().toString();
                    Toast.makeText(getApplicationContext(), time , Toast.LENGTH_SHORT).show();
                   // String time = calendar.getTime().toString();

                     */




                }
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                itemsList.remove(position);
                adapter.notifyDataSetChanged();


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();



        btnSetInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetInterval(position);
                alertDialog.dismiss();
            }
        });

        btnSetTimeOfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSetTimeOfDay(position);
                alertDialog.dismiss();
            }
        });


        // Create and show the AlertDialog
        alertDialog.show();
    }




    private void showDialogSetInterval(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_set_days_left, null);
        final EditText enterDaysUntil = dialogView.findViewById(R.id.set_days_until);


        enterDaysUntil.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                enterDaysUntil.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(enterDaysUntil, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        enterDaysUntil.requestFocus();


        builder.setView(dialogView);

        builder.setMessage("Set Reminder");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int interval = Integer.parseInt(enterDaysUntil.getText().toString());

                Item item = itemsList.get(position);

                long millisUntil = item.daysToMillis(interval);
                item.setmInterval(interval);
                item.setmMillisEnd(millisUntil);
                //adapter.notifyDataSetChanged();
                showDialogItem(item, position);
                if (dialog != null) {
                    dialog.dismiss();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                showDialogItem(itemsList.get(position), position);
            }
        });


        // Create and show the AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void showDialogSetTimeOfDay(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_set_time_of_day, null);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        builder.setView(dialogView);

        //hardcoded
        builder.setMessage("Set Time Of Day");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                Item item =  itemsList.get(position);
                item.setHourOfDay(hour);
                item.setmMinuteOfDay(minute);
                showDialogItem(item, position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        // Create and show the AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNotification(long millis, String title, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        //  timerViewLabel.setText("Set");
        Log.i("Blubb", "Alert Set ");
    }

    //useless
    public void alertUser() {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification("Hallo", "Du");
        notificationHelper.getManager().notify(1, nb.build());
    }


}                /*
                Intent openCountDownView = new Intent(MainActivity.this, TimerView.class);
                openCountDownView.putExtra("label", timerNameList[position]);
                openCountDownView.putExtra("duration", timerDurationList[position]);
                startActivity(openCountDownView);
                */

