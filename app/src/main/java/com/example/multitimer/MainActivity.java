package com.example.multitimer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private Menu optionsMenu;
    private ArrayList<Item> itemsList;

    private ItemAdapter adapter;

    private Boolean newItem;

   // public Boolean expanded;

    private Item expandedItem;

    EditText enterTitle;

    ListView listView;

    private NotificationHelper notificationHelper;

    private int sortMode;

    private int sortInverted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);

        newItem = false;

        itemsList = SharedPreferencesHelper.loadData(this);
        adapter = new ItemAdapter(this, itemsList);

        sortMode = SharedPreferencesHelper.getSortMode(this);
        sortInverted = SharedPreferencesHelper.getSortInverted(this);

        sortItems();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = SharedPreferencesHelper.getCurrentID(getApplicationContext());
                newItem = true;
                showDialogTitle(new Item(id, "", System.currentTimeMillis(), -1, 0, 0, -1, 0));
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Item item = itemsList.get(position);
                expandListItem(item);
            }

        });

        //TODO rename
        //registers intent to restart reminder when app is open
        registerReceiver(broadcastReceiver_, new IntentFilter("RESTART_REMINDER"));

        registerReceiver(broadcastReceiver, new IntentFilter("RESET_ALERT_ACTIVE"));


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
        unregisterReceiver(broadcastReceiver_);

    }

    //resets 'alertActive' when app is open. AlertActive needs to be reset after alert was received in ALertReceiver
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("id")) {
                int id = intent.getIntExtra("id", -1);
                Item item = getItemByID(id);
                item.setmAlertActive(0);
                adapter.notifyDataSetChanged();
            }
        }
    };

    BroadcastReceiver broadcastReceiver_ = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("id")) {
                int ID = intent.getIntExtra("id", -1);
                Item item = getItemByID(ID);
                item.setmMillisStart(System.currentTimeMillis());
                item.setmMillisEnd(Item.daysToMillis(item.getmInterval()));
                item.setmAlertActive(1);
                Log.i("blab", "blub");
            }
        }
    };




    private void expandListItem(Item item) {

        if (expandedItem != item && expandedItem != null) {
            expandListItem(expandedItem);
        }
        if (expandedItem == null) {
            item.setmExpanded(true);
            adapter.notifyDataSetChanged();
            expandedItem = item;
        } else {
            item.setmExpanded(false);
            adapter.notifyDataSetChanged();
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
        item.setmMillisEnd(Item.daysToMillis(item.getmInterval()));
        item.setmAlertActive(1);
        adapter.notifyDataSetChanged();
        SharedPreferencesHelper.setLong(getApplicationContext(), "millis_start_" + item.getmID(), item.getmMillisStart());
        SharedPreferencesHelper.setLong(getApplicationContext(), "millis_end_" + item.getmID(), item.getmMillisEnd());
        SharedPreferencesHelper.setInt(getApplicationContext(), "alert_active_" + item.getmID(), 1);
        setAlert(item.getmMillisEnd(), item.getmTitle(), item.getmID());
    }

    public void sortItems() {
        if (sortMode == 0) {
            Collections.sort(itemsList, new Item.CompDaysUntilAlert());
            adapter.notifyDataSetChanged();
        } else if (sortMode == 1) {
            Collections.sort(itemsList, new Item.CompDaysPassed());
            adapter.notifyDataSetChanged();
        }
        if (sortInverted == 1 ){
            Collections.reverse(itemsList);
        }
    }

    public void deleteItem(Item item) {
        //remove item from List
        itemsList.remove(item);
        adapter.notifyDataSetChanged();
        //cancel Alert
        cancelNotification(item.getmTitle(), item.getmID());
        //remove all Data
        SharedPreferencesHelper.removeDataOfItemByID(this, item.getmID());
    }



    //TODO newitem = false    wenn dialog focus verliert             alertDialog.setCanceledOnTouchOutside(false) ;
    // Maybe just two sepparate dialogs?
    // Make ok-btn only clickable when title is entered
    private void showDialogTitle(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_new, null);
        final EditText enterTitle = dialogView.findViewById(R.id.edit_text_title);
        enterTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.font_medium));
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
        builder.setCancelable(false);

        if (newItem) {
            builder.setMessage(getString(R.string.title_new));
            // close expanded item
            if (expandedItem != null) {
                expandListItem(expandedItem);
            }
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
                newItem = false;
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


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
                        expandListItem(item);
                        sortItems();
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

    public void showDialogNumberPicker(final Item item, final Boolean setDaysToAlert) {
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
                    setAlert(item.getmMillisEnd(), item.getmTitle(), item.getmID());
                    //activate alert in case it's not active
                    item.setmAlertActive(1);
                    SharedPreferencesHelper.setInt(getApplicationContext(), "alert_active_" + item.getmID(), 1);
                    // set Interval to daysUntilAlert when no interval is set
                    if (item.getmInterval() == -1) {
                        item.setmInterval(picked);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;

        if (sortMode == 0) {
            optionsMenu.findItem(R.id. sub_action_sort_by_daysUntilAlert).setChecked(true);
        } else if (sortMode == 1) {
            optionsMenu.findItem(R.id.sub_action_sort_by_daysPassed).setChecked(true);
        }
        if (sortInverted == 1) {
            optionsMenu.findItem(R.id. sub_action_sort_inverted).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        // not as switch statement because 'keepMenuOpen' Method doesn't work if something is returned as required by switch statement
        if (id == R.id.sub_action_sort_by_daysUntilAlert) {
            if (!menuItem.isChecked()) {
                sortMode = 0;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 0);
                menuItem.setChecked(true);
                MenuItem checkedItem = optionsMenu.findItem(R.id.sub_action_sort_by_daysPassed);
                checkedItem.setChecked(false);
            }
            keepMenuOpen(menuItem);
        }

        else if (id == R.id.sub_action_sort_by_daysPassed) {
                if (!menuItem.isChecked()) {
                    sortMode = 1;
                    sortItems();
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 1);
                    menuItem.setChecked(true);
                    MenuItem checkedItem = optionsMenu.findItem(R.id.sub_action_sort_by_daysUntilAlert);
                    checkedItem.setChecked(false);
                }
                keepMenuOpen(menuItem);
            } else if (id == R.id.sub_action_sort_inverted) {
                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    sortInverted = 1;
                    sortItems();
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_inverted", 1);
                } else {
                    menuItem.setChecked(false);
                    sortInverted = 0;
                    sortItems();
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_inverted", 0);
                }
                keepMenuOpen(menuItem);
            } else if (id == R.id.action_clear_all_data){
                SharedPreferencesHelper.clearAllData(getApplicationContext());
                itemsList.clear();
                adapter.notifyDataSetChanged();
            } else if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.action_throw_notification) {
                  setAlert(System.currentTimeMillis() + 1500, "Bla", 1);
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }


 /*
    @Override
    public boolean onOptionsItemSelected_(MenuItem menuItem) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(menuItem.getItemId()) {

            case R.id.action_settings:
                return true;
            case R.id.action_throw_notification:
              //  setAlert(System.currentTimeMillis() + 1500, "Bla", 1234);
                return true;
            case R.id.sub_action_sort_by_daysUntilAlert:
                if (!menuItem.isChecked()) {
                    Collections.sort(itemsList, new Item.CompDaysUntilAlert());
                    adapter.notifyDataSetChanged();
                    sortMode = 0;
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 0);
                    menuItem.setChecked(true);
                    MenuItem checkedItem = optionsMenu.findItem(R.id. sub_action_sort_by_daysPassed);
                    checkedItem.setChecked(false);
                }
                return true;

            case R.id.sub_action_sort_by_daysPassed:
                Log.i("press", "daysPassed");

                if (!menuItem.isChecked()) {
                    Collections.sort(itemsList, new Item.CompDaysPassed());
                    adapter.notifyDataSetChanged();
                    sortMode = 1;
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 1);
                    menuItem.setChecked(true);
                    MenuItem checkedItem = optionsMenu.findItem(R.id. sub_action_sort_by_daysUntilAlert);
                    checkedItem.setChecked(false);
                }
                return true;
            case R.id.sub_action_sort_inverted:
                Log.i("press", "Inverted");

                if (!menuItem.isChecked()) {
                    menuItem.setChecked(true);
                    Collections.reverse(itemsList);
                    adapter.notifyDataSetChanged();
                    sortInverted = 1;
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_inverted", 1);
                } else {
                    menuItem.setChecked(false);
                    Collections.reverse(itemsList);
                    adapter.notifyDataSetChanged();
                    sortInverted = 0;
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_inverted", 0);
                }
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);

        }
    }

  */

    private boolean keepMenuOpen(MenuItem menuItem) {
        //this keeps the menu open after selection by creating a dummyView that is marked as expandable but is not expanded
        // see: https://stackoverflow.com/questions/52176838/how-to-hold-the-overflow-menu-after-i-click-it/52177919#52177919
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menuItem.setActionView(new View(this));
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return false;
            }
        });
        return true;
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
        //TODO useless??
     //   intent.putExtra("title", title);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }


    public Item getItemByID(int ID) {
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
