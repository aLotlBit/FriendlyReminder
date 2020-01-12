package com.example.multitimer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    static private String TAG = "MAIN_ACTIVITY";
    static private long ONE_DAY = 86400000L;
    static private long THREE_HOURS = 10800000L;




    private Menu optionsMenu;
    private ArrayList<Item> itemsList;

    private ItemAdapter adapter;

    private Boolean newItem;

    private Item expandedItem;

    ListView listView;

    private NotificationHelper notificationHelper;

    private int sortMode;

    private int sortDirection;

    private MenuItem checkedMenuItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(SharedPreferencesHelper.getTheme(getApplicationContext()) == 1) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        //needed for backporting Java.Time functionality
        AndroidThreeTen.init(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = SharedPreferencesHelper.getCurrentID(getApplicationContext());
                newItem = true;
                showDialogTitle(new Item(id, "", System.currentTimeMillis(), -1,  -1, 0));
            }
        });

        newItem = false;

        itemsList = SharedPreferencesHelper.loadData(this);
        final ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new ItemAdapter(this, itemsList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Item item = itemsList.get(position);
                expandListItem(item);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("DISABLE_ALERT_ACTIVE");
        filter.addAction("RESTART_RUNNING");
        filter.addAction("PLUS_ONE_DAY");
        filter.addAction("PLUS_THREE_HOURS");
        filter.addAction("CONTENT");
        registerReceiver(broadcastReceiver, filter);

        //restarts reminder when app was closed
        Intent intent = getIntent();
        if (intent.getAction() == "CONTENT") {
            int id = intent.getIntExtra("id", -1);
            Item item = getItemByID(id);
            expandListItem(item);
            Log.d(TAG, "CONTENT received from launch with id" + Boolean.toString(item.getmExpanded()));
        }

        sortMode = SharedPreferencesHelper.getSortMode(this);
        sortDirection = SharedPreferencesHelper.getSortDirection(this);
        sortItems();
    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    //resets values when app is open. AlertActive needs to be reset after alert was received in ALertReceiver
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "intent received:  " );

            switch (intent.getAction()) {
                case "DISABLE_ALERT_ACTIVE":
                    //when alert was received alertActive needs to be set to false
                    int id = intent.getIntExtra("id", -1);
                    Item item = getItemByID(id);
                    item.setmAlertStatus(4);
                    adapter.notifyDataSetChanged();
                    break;
                case "RESTART_RUNNING":
                    //when restart button is pressed millis and alertActive need to be set
                    int id_1 = intent.getIntExtra("id", -1);
                    Item item_1 = getItemByID(id_1);
                    item_1.setmMillisStart(System.currentTimeMillis());
                    item_1.setmMillisEnd(item_1.calcMillisEnd(item_1.getmInterval()));
                    item_1.setmAlertStatus(3);
                    adapter.notifyDataSetChanged();
                    break;
                case "PLUS_ONE_DAY":
                    int id_2 = intent.getIntExtra("id", -1);
                    Item item_2 = getItemByID(id_2);
                    item_2.setmMillisEnd(item_2.getmMillisEnd() + ONE_DAY);
                    item_2.setmAlertStatus(3);
                    adapter.notifyDataSetChanged();
                    break;
                case "PLUS_THREE_HOURS":
                    Log.d(TAG, "Plus3H Received");
                    int id_3 = intent.getIntExtra("id", -1);
                    Item item_3 = getItemByID(id_3);
                    item_3.setmMillisEnd(item_3.getmMillisEnd() + THREE_HOURS);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(item_3.getmMillisEnd());
                    item_3.setmHourOfDay(calendar.get(Calendar.HOUR_OF_DAY));
                    item_3.setmMinuteOfDay(calendar.get(Calendar.MINUTE));
                    item_3.setmAlertStatus(3);
                    adapter.notifyDataSetChanged();
                    break;
                case "CONTENT":
                    Log.d(TAG, "CONTENT Received");
                    int id_4 = intent.getIntExtra("id", -1);
                    Item item_4 = getItemByID(id_4);
                    //check if item is already expanded before expanding
                    if (item_4 != expandedItem) expandListItem(item_4);
                    break;
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


    //TODO call methods directly from adapter with item als argument
    public void changeTitle(View v, int position) {
        Item item = itemsList.get(position);
        showDialogTitle(item);
    }

    public void changeDaysLeft(View v, int position) {
        Item item = itemsList.get(position);
        showDialogNumberPicker(item, 0);
    }

    public void changeTimeOfDay(View v, int position) {
        Item item = itemsList.get(position);
        showDialogTimePicker(v, item);
    }

    public void changeInterval(View v, int position) {
        Item item = itemsList.get(position);
        showDialogNumberPicker(item, 1);
    }


    public void cancelAlert(String title, int id) {
        cancelNotification(title, id);
    }

    public void restartAlert(Integer ID) {
        Item item = getItemByID(ID);
        item.setmMillisStart(System.currentTimeMillis());
        if (item.getmInterval() != -1) {
            item.setmMillisEnd(item.calcMillisEnd(item.getmInterval()));
            SharedPreferencesHelper.setLong(getApplicationContext(), "millis_start_" + item.getmID(), item.getmMillisStart());
            setAlert(item, item.getmMillisEnd());
        } else {
            showDialogNumberPicker(item, 1);
        }
    }

    public void sortItems() {
        if (sortMode == 0) {
            Collections.sort(itemsList, new Item.CompAlertStatus().thenComparing(new Item.CompDaysUntilAlert()));
            //Collections.sort(itemsList, new Item.CompDaysUntilAlert());
        } else if (sortMode == 1) {
            Collections.sort(itemsList, new Item.CompDaysPassed());
        } else if (sortMode == 2)
         //   Collections.sort(itemsList, new Item.CompAlertStatus());
            Collections.sort(itemsList, new Item.CompAlphabet());

        if (sortDirection == 0 ){
            Collections.reverse(itemsList);
        }
        adapter.notifyDataSetChanged();
    }

    public void deleteItem(Item item) {
        //remove item from List
        itemsList.remove(item);
        adapter.notifyDataSetChanged();
        //cancel Alert
        cancelNotification(item.getmTitle(), item.getmID());
        //remove all Data
        SharedPreferencesHelper.removeDataOfItemByID(this, item.getmID());
        Toast.makeText(getApplicationContext(), getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
    }

    private void clearData(){
        SharedPreferencesHelper.clearAllData(getApplicationContext());
        //cancel all alerts
        for (int i = 0; i < itemsList.size(); i++) {
            Item item = itemsList.get(i);
            cancelNotification(item.getmTitle(), item.getmID());
        }
        itemsList.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), getString(R.string.data_cleared), Toast.LENGTH_SHORT).show();
    }


    private void showDialogTitle(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_title, null);
        final EditText enterTitle = dialogView.findViewById(R.id.edit_text_title);
        //TODO set style for TextSize
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
            if (enterTitle.hasFocus()) {
                enterTitle.setSelection(item.getmTitle().length());
            }
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
                if (title.length() == 0) {
                    if (!newItem) {
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_title), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Context context = getApplicationContext();
                    int id = item.getmID();
                    item.setmTitle(title);
                    if (newItem) {
                        itemsList.add(item);
                        SharedPreferencesHelper.addIdToSet(context, id);
                        SharedPreferencesHelper.setLong(context, "millis_start_" + String.valueOf(id), item.getmMillisStart());
                        expandListItem(item);
                        sortItems();
                        newItem = false;
                    } else if (item.getmAlertStatus() == 3) {
                        //reset Alert with new title so correct title shows in notification
                        setAlert(item, item.getmMillisEnd());
                    }
                    SharedPreferencesHelper.setString(context, "title_" + String.valueOf(id), title);
                    adapter.notifyDataSetChanged();
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }

    public void showDialogNumberPicker(final Item item, final int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.number_picker);
        final ImageView iv_keyboard = dialogView.findViewById(R.id.iv_keyboard);
        final EditText editText = dialogView.findViewById(R.id.editText);
        //TODO hardcoded
        numberPicker.setMaxValue(31);
        numberPicker.setMinValue(0);
        if (mode == 0) {
            builder.setTitle(getString(R.string.days_until_change));
            if (item.getDaysLeft() == -1 ){
                Resources res = getResources();
                numberPicker.setValue(res.getInteger(R.integer.DEFAULT_DAYS_UNTIL_ALERT));
                editText.setText(String.valueOf(res.getInteger(R.integer.DEFAULT_DAYS_UNTIL_ALERT)));
            } else {
                numberPicker.setValue(item.getDaysLeft());
                editText.setText(item.getDaysLeft().toString());
            }
        } else if (mode == 1){
            builder.setTitle(getString(R.string.interval_change));
            if (item.getmInterval() == -1) {
                Resources res = getResources();
                numberPicker.setValue(res.getInteger(R.integer.DEFAULT_INTERVAL));
                editText.setText(String.valueOf(res.getInteger(R.integer.DEFAULT_INTERVAL)));
            } else {
                numberPicker.setValue(item.getmInterval());
                editText.setText(item.getmInterval().toString());
            }

            } else {
            builder.setTitle(getString(R.string.days_passed_change));
            numberPicker.setValue(Integer.parseInt(item.getDaysPassed()));
            editText.setText(item.getDaysPassed());

        }

        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int picked;
                if (numberPicker.getVisibility() == View.VISIBLE) {
                    picked = numberPicker.getValue();
                } else {
                    picked = Integer.parseInt(editText.getText().toString());
                }
                if (mode == 0) {
                    // item.setmMillisEnd(item.calcMillisEnd(picked));
                    setAlert(item, item.calcMillisEnd(picked));
                    // set Interval to daysUntilAlert when no interval is set
                    if (item.getmInterval() == -1) {
                        item.setmInterval(picked);
                    }
                } else if (mode == 1){
                    item.setmInterval(picked);
                    SharedPreferencesHelper.setInt(getApplicationContext(), "interval_" + item.getmID(), picked);
                } else {
                    item.setmMillisStart(System.currentTimeMillis() - Item.daysToMillis(picked));
                    SharedPreferencesHelper.setLong(getApplicationContext(), "millis_start_" + item.getmID(), item.getmMillisStart());
                }
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        iv_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberPicker.getVisibility() == View.VISIBLE) {
                    iv_keyboard.setImageResource(R.drawable.numbers);
                    numberPicker.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            editText.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                        }
                    });
                    editText.requestFocus();

                } else {
                    iv_keyboard.setImageResource(R.drawable.keyboard);
                    numberPicker.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                }
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
     //   Button keyboard = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
     //   keyboard.setBackgroundResource(R.drawable.keyboard);
     //   keyboard.setBackgroundColor(ItemAdapter.getColorByAttributeId(getApplicationContext(), R.attr.colorIcon));
     //   keyboard.setLayoutParams(new LinearLayout.LayoutParams(70,70));
    }

    //TODO 'view' useless
    private void showDialogTimePicker(View view, final Item item) {
     //   final TextView textView = (TextView) view;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_set_time_of_day, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        //TODO remove Calendar
        Calendar calendar = Calendar.getInstance();
        timePicker.setMinute(calendar.get(Calendar.MINUTE) + 1);
        builder.setView(dialogView);
        builder.setTitle(getString(R.string.time_of_day_change));
        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //hold tmp time of day to reset to in case alert is not set
                item.setmHourOfDay(timePicker.getHour());
                item.setmMinuteOfDay(timePicker.getMinute());
                setAlert(item, item.calcMillisEnd(item.getDaysLeft()));
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

    public void showDialogConfirmation(final Item item, final int confirmationFor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (confirmationFor == 0) {
            builder.setTitle(item.getmTitle());
            builder.setMessage(getString(R.string.confirmation_msg_delete_item));
        } else {
            builder.setTitle(getString(R.string.confirmation_title_clear_all));
            builder.setMessage(getString(R.string.confirmation_msg_clear_all1) + "\n" + getString(R.string.confirmation_msg_clear_all2));
        }
        builder.setPositiveButton(getString(R.string.dialog_btn_positve), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (confirmationFor == 0) {
                    deleteItem(item);
                } else {
                    clearData();
                }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        optionsMenu = menu;
        MenuCompat.setGroupDividerEnabled(menu, true);

        if (sortMode == 0) {
            checkedMenuItem = optionsMenu.findItem(R.id. sub_action_sort_by_daysUntilAlert).setChecked(true);
        } else if (sortMode == 1) {
            checkedMenuItem = optionsMenu.findItem(R.id.sub_action_sort_by_daysPassed).setChecked(true);
        } else if (sortMode == 2) {
            checkedMenuItem = optionsMenu.findItem(R.id.sub_action_sort_by_alphabet).setChecked(true);
        }
        if (sortDirection == 1) {
            optionsMenu.findItem(R.id. sub_action_sort_ascending).setChecked(true);
        } else {
            optionsMenu.findItem(R.id. sub_action_sort_descending).setChecked(true);
        }
        if (SharedPreferencesHelper.getTheme(getApplicationContext()) == 1) {
            optionsMenu.findItem(R.id.action_change_theme).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        // not as switch statement because 'keepMenuOpen' Method doesn't work if 'break' as required by switch statement
        if (id == R.id.sub_action_sort_by_daysUntilAlert) {
            if (!menuItem.isChecked()) {
                sortMode = 0;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 0);
                //uncheck already checked item
                if (checkedMenuItem != null) {
                checkedMenuItem.setChecked(false); }
                //set checkedMenuItem to current menuItem
                menuItem.setChecked(true);
                checkedMenuItem = menuItem;
            }
            keepMenuOpen(menuItem);
        } else if (id == R.id.sub_action_sort_by_daysPassed) {
            if (!menuItem.isChecked()) {
                sortMode = 1;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 1);
                //uncheck already checked item
                if (checkedMenuItem != null) {
                    checkedMenuItem.setChecked(false); }
                //set checkedMenuItem to current menuItem
                menuItem.setChecked(true);
                checkedMenuItem = menuItem;
            }
            keepMenuOpen(menuItem);
        } else if (id == R.id.sub_action_sort_by_alphabet) {
            if (!menuItem.isChecked()) {
                sortMode = 2;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_mode", 2);
                //uncheck already checked item
                if (checkedMenuItem != null) {
                    checkedMenuItem.setChecked(false); }
                //set checkedMenuItem to current menuItem
                menuItem.setChecked(true);
                checkedMenuItem = menuItem;

            }
            keepMenuOpen(menuItem);
        } else if (id == R.id.sub_action_sort_ascending) {
            if (!menuItem.isChecked()) {
                menuItem.setChecked(true);
                optionsMenu.findItem(R.id.sub_action_sort_descending).setChecked(false);
                sortDirection = 1;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_direction", 1);
            }
            keepMenuOpen(menuItem);
        } else if (id == R.id.sub_action_sort_descending) {
            if (!menuItem.isChecked()) {
                menuItem.setChecked(true);
                optionsMenu.findItem(R.id.sub_action_sort_ascending).setChecked(false);
                sortDirection = 0;
                sortItems();
                SharedPreferencesHelper.setInt(getApplicationContext(), "sort_direction", 0);
            }
            keepMenuOpen(menuItem);

            } else if (id == R.id.action_clear_all_data){
                showDialogConfirmation(null, 1);
            } else if (id == R.id.action_change_theme) {
            if (!menuItem.isChecked()){
                 setTheme(R.style.DarkTheme);
                 menuItem.setChecked(false);
                 SharedPreferencesHelper.setInt(getApplicationContext(), "theme", 1);
                 MainActivity.this.recreate();
                } else {
                    setTheme(R.style.LightTheme);
                    menuItem.setChecked(true);
                    SharedPreferencesHelper.setInt(getApplicationContext(), "theme", 0);
                    MainActivity.this.recreate();
                }
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
                    sortDirection = 1;
                    SharedPreferencesHelper.setInt(getApplicationContext(), "sort_inverted", 1);
                } else {
                    menuItem.setChecked(false);
                    Collections.reverse(itemsList);
                    adapter.notifyDataSetChanged();
                    sortDirection = 0;
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


    public boolean setAlert(Item item, long millis_end) {
        //only set alerts that are not in the past
        if (millis_end - System.currentTimeMillis() >= 0) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
            intent.putExtra("title", item.getmTitle());
            intent.putExtra("id", item.getmID());
            intent.setAction("ALERT");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, item.getmID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis_end, pendingIntent);

            //when alert is set set values in item
            item.setmMillisEnd(millis_end);
            item.setmAlertStatus(3);
            adapter.notifyDataSetChanged();
            //when alert is set set values in shared prefs
            SharedPreferencesHelper.setLong(getApplicationContext(), "millis_end_" + item.getmID(), item.getmMillisEnd());
            SharedPreferencesHelper.setInt(getApplicationContext(), "alert_status_" + item.getmID(), 3);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getmMillisEnd());
            String time = calendar.getTime().toString();

       //     Toast.makeText(getApplicationContext(), getString(R.string.set_alert) + time, Toast.LENGTH_LONG).show();
            return true;
        } else {

            return false;
        }
    }


    private void cancelNotification(String title, int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("id", id);
        //titel and action necessary to recreate the exact intent in order for it to be canceled
        intent.putExtra("title", title);
        intent.setAction("ALERT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
  //      Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    //TODO move to itemClass als static method with itemsList als argument
    public Item getItemByID(int ID) {
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getmID() == ID) {
                return itemsList.get(i);
            }
        } return null;
    }



}
