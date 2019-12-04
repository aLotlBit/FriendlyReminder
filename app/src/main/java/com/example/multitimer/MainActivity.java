package com.example.multitimer;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {


    private ArrayList<Item> itemsList;

    private ItemAdapter adapter;

    private Boolean newItem;

   // public Boolean expanded;

    private View expandedItem;

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
      //  expanded = false;

        loadFromSharedPrefs(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = SharedPreferencesHelper.getCurrentID(getApplicationContext());
                showDialogTitle(new Item(id, "", System.currentTimeMillis(), -1, 0, 0, -1, 0));
                newItem = true;
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                expandListItem(view);
            }

        });

        //registers intent to restart reminder when app is open
        registerReceiver(broadcastReceiver, new IntentFilter("RESTART_REMINDER"));

        //restarts reminder when app was closed
        Intent intent = getIntent();
        if (intent.hasExtra("RESTART_REMINDER")) {
            Toast.makeText(getApplicationContext(), "WUah", Toast.LENGTH_LONG).show();
            int ID = intent.getIntExtra("id", -1);
            restartAlert(0);
            moveTaskToBack(true);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        //saveToSharedPrefs();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("id")) {
                int ID = intent.getIntExtra("id", -1);
                restartAlert(ID);
                Log.i("blab", "blub");
            }
        }
    };

    private void expandListItem(View itemView) {
        View groupExpanded = itemView.findViewById(R.id.expanded);
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvDaysLeft = itemView.findViewById(R.id.tv_days_left);
        TextView tvTimeOfDay = itemView.findViewById(R.id.tv_time_of_day);
        TextView tvInterval = itemView.findViewById(R.id.tv_interval);
        TextView tvDaysPassed = itemView.findViewById(R.id.tv_days_passed);
        ImageView ivAlarm = itemView.findViewById(R.id.iv_alarm);
        ImageView ivArrow = itemView.findViewById(R.id.iv_arrow);

        if (expandedItem != null & expandedItem != itemView) {
            expandListItem(expandedItem);
        }


        if (expandedItem == null) {
            groupExpanded.setVisibility(View.VISIBLE);
            tvDaysLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_medium));
            tvTimeOfDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_medium));
            //TODO why does is textsize differen when set with xml?
            tvInterval.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_medium));
            ivAlarm.setImageResource(R.drawable.edit);
            ivArrow.setImageResource((R.drawable.arrow_up));

            tvDaysPassed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh, 0, 0, 0);

            tvTitle.setClickable(true);
            tvDaysLeft.setClickable(true);
            tvTimeOfDay.setClickable(true);

            itemView.setBackgroundColor(0xffcccccc);

        //    expanded = true;

            expandedItem = itemView;

        } else {

            groupExpanded.setVisibility(View.GONE);
            tvDaysLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_small));
            tvTimeOfDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_small));
            //TODO why does is textsize differen when set with xml?
            tvInterval.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_medium));
            ivAlarm.setImageResource(R.drawable.notification);
            ivArrow.setImageResource((R.drawable.arrow_down));
            tvDaysPassed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timer, 0, 0, 0);
            tvTitle.setClickable(false);
            tvDaysLeft.setClickable(false);
            tvTimeOfDay.setClickable(false);
            itemView.setBackgroundColor(0xffffffff);

         //   expanded = false;
            expandedItem = null;
        }


    }

    public void changeTitle(View v, int position) {
        Item item = itemsList.get(position);
        showDialogTitle(item);
    }

    public void changeDaysLeft(View v, int position) {
        Item item = itemsList.get(position);
        showDialogNumberPicker(item, true);
    }

    public void changeTimeOfDay(View v, int position) {
        Item item = itemsList.get(position);
        showDialogTimePicker(v, item);
    }

    public void changeInterval(View v, int position) {
        Item item = itemsList.get(position);
        showDialogNumberPicker(item, false);
    }

    public void setAlert(Long millis, String title, int id) {
        setNotification(millis, title, id);
    }

    public void cancelAlert(String title, int id) {
        cancelNotification(title, id);
    }

    public void restartAlert(Integer ID) {
        Item item = getItemByID(ID);
        item.setmMillisStart(System.currentTimeMillis());
        item.setmMillisEnd(item.daysToMillis(item.getmInterval()));
        adapter.notifyDataSetChanged();
        SharedPreferencesHelper.setLong(getApplicationContext(), "millis_start_" + item.getmID(), item.getmMillisStart());
        SharedPreferencesHelper.setLong(getApplicationContext(), "millis_end_" + item.getmID(), item.getmMillisEnd());
        setAlert(item.getmMillisEnd(), item.getmTitle(), item.getmID());
    }



    public void loadFromSharedPrefs(Context mContext) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit = mSharedPrefs.edit();

        Set<String> s = new HashSet<String>();
        Set<String> set_ids = mSharedPrefs.getStringSet("set_ids", s);
        List<String> list_ids = new ArrayList<String>(set_ids);

        itemsList.clear();

        for (int i = 0; i < list_ids.size(); i++) {
            String id = list_ids.get(i);
            String title = mSharedPrefs.getString("title_" + id, null);
            long millisStart = mSharedPrefs.getLong("millis_start_" + id, -1);
            long millisEnd = mSharedPrefs.getLong("millis_end_" + id, -1);
            int interval = mSharedPrefs.getInt("interval_" + id, -1);
            int alertActive = mSharedPrefs.getInt("alert_active_" + id, 0);

            itemsList.add(new Item(Integer.parseInt(id), title, millisStart, millisEnd, 0, 0, interval, alertActive));

            mEdit.commit();

        }

    }


    private void showDialogTitle(final Item item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_new, null);
        final EditText enterTitle = dialogView.findViewById(R.id.edit_text_title);

        enterTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                enterTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(enterTitle, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        enterTitle.requestFocus();

        builder.setView(dialogView);

        if (newItem) {
            builder.setMessage(getString(R.string.title_new));
        } else {
            builder.setMessage(getString(R.string.title_change));
            enterTitle.setText(item.getmTitle());
        }


        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
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

        // close expanded item
        if (expandedItem != null) {
            expandListItem(expandedItem);
        }

        //needed so dialog can stay open when positve button is pressed
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = enterTitle.getText().toString();
                if (title.length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter Title!!!", Toast.LENGTH_SHORT).show();

                } else {
                    Context context = getApplicationContext();
                    int ID = item.getmID();

                    item.setmTitle(title);

                    if (newItem) {
                        itemsList.add(item);
                        SharedPreferencesHelper.addIdToSet(context, ID);
                        SharedPreferencesHelper.setLong(context, "millis_start_" + String.valueOf(ID), item.getmMillisStart());
                        newItem = false;
                    }

                    SharedPreferencesHelper.setString(context, "title_" + String.valueOf(ID), title);
                    adapter.notifyDataSetChanged();


                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }

    private void showDialogNumberPicker(final Item item, final Boolean setDaysToAlert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);

        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(0);

        if (setDaysToAlert) {
            builder.setMessage(getString(R.string.days_until_change));
            numberPicker.setValue(item.getDaysLeft());

        } else {
            // if !setDaysToAlert = setInterval
            builder.setMessage(getString(R.string.interval_change));
            numberPicker.setValue(item.getmInterval());

        }
        builder.setView(dialogView);


        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int picked = numberPicker.getValue();
                if (setDaysToAlert) {
                    item.setmMillisEnd(item.daysToMillis(picked));
                    SharedPreferencesHelper.setLong(getApplicationContext(), "millis_end_" + item.getmID(), item.getmMillisEnd());
                    if (item.getmAlertActive()) {
                        setAlert(item.getmMillisEnd(), item.getmTitle(), item.getmID());
                    }

                } else {
                    item.setmInterval(picked);
                    SharedPreferencesHelper.setInt(getApplicationContext(), "interval_" + item.getmID(), picked);
                }
                adapter.notifyDataSetChanged();
            }
        });


        builder.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }





    private void showDialogTimePicker(View view, final Item item) {

        final TextView textView = (TextView) view;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_set_time_of_day, null);

        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        builder.setView(dialogView);

        //hardcoded
        builder.setMessage(getString(R.string.time_of_day_change));
        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                textView.setText(hour + ":" + minute);

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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_throw_notification) {
            setAlert(System.currentTimeMillis() + 1500, "Bla", 1234);
        } else if (id == R.id.sub_action_sort_by_reverse) {
            Collections.reverse(itemsList);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.sub_action_sort_by_daysPassed) {
            //adapter.sort( new Item.CompDaysPassed());
            Collections.sort(itemsList, new Item.CompDaysPassed());
            adapter.notifyDataSetChanged();
        } else if (id == R.id.sub_action_sort_by_daysUntilAlert) {
            Collections.sort(itemsList, new Item.CompDaysUntilAlert());
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


    private void setNotification(long millis, String title, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
    }


    private void cancelNotification(String title, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();

    }


    private Item getItemByID(int ID) {
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getmID() == ID) {
                return itemsList.get(i);
            }
        } return null;
    }


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
