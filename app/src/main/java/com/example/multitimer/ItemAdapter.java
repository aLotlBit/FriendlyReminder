package com.example.multitimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {


    private Context mContext;
    private List<Item> itemsList = new ArrayList<>();

    public ItemAdapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0, list);
        this.mContext = context;
        itemsList = list;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            listItem = vi.inflate(R.layout.listview_item_layout, null);

        }
        final Item currentItem = itemsList.get(position);

        if (currentItem != null) {


            TextView tv_title = listItem.findViewById(R.id.tv_title);
            TextView tv_daysPassed = listItem.findViewById(R.id.tv_days_passed);
            TextView tv_daysUntilAlert = listItem.findViewById(R.id.tv_days_left);


            if (tv_title != null) {
                tv_title.setText(currentItem.getmTitle());
            }
            if (tv_daysPassed != null) {
                tv_daysPassed.setText(currentItem.getDaysPassed());
            }
            if (tv_daysUntilAlert != null) {
                tv_daysUntilAlert.setText(getStringForTvDaysUntilAlert(currentItem));
            }


            TextView tv_timeOfDay = listItem.findViewById(R.id.tv_time_of_day);
            TextView tv_interval = listItem.findViewById(R.id.tv_interval);
            final CheckBox cb_alertActive = listItem.findViewById(R.id.cb_alert_active);
            TextView tv_restart = listItem.findViewById(R.id.tv_reset);



            if (tv_interval != null) {
                tv_interval.setText(getStringForTvInterval(currentItem));
            }

            cb_alertActive.setChecked(false);

            if (cb_alertActive != null) {
                if (currentItem.getmAlertActive() == 1) {
                    cb_alertActive.setChecked(true);
                } else {
                    cb_alertActive.setChecked(false);
                }

                cb_alertActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            cb_alertActive.setChecked(true);
                            currentItem.setmAlertActive(1);
                            SharedPreferencesHelper.setInt(mContext, "alert_active_" + currentItem.getmID(), 1);
                            ((MainActivity) mContext).setAlert(currentItem.getmMillisEnd(), currentItem.getmTitle(), currentItem.getmID());
                        } else {
                            cb_alertActive.setChecked(false);
                            currentItem.setmAlertActive(0);
                            SharedPreferencesHelper.setInt(mContext, "alert_active_" + currentItem.getmID(), 0);
                            ((MainActivity) mContext).cancelAlert(currentItem.getmTitle(), currentItem.getmID());
                        }
                    }
                });
            }


            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTitle(view, position);
                    }
                }
            });

            tv_daysUntilAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeDaysLeft(view, position);
                    }
                }
            });

            tv_timeOfDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTimeOfDay(view, position);
                    }
                }
            });

            tv_interval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeInterval(view, position);
                    }
                }
            });


            tv_restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).restartAlert(currentItem.getmID());
                    }
                }
            });


            tv_daysPassed.setClickable(false);
            tv_daysUntilAlert.setClickable(false);
            tv_timeOfDay.setClickable(false);


        }

        return listItem;
    }


    private String getStringForTvDaysUntilAlert(Item item) {
        int daysUntilAlert = item.getDaysLeft();
        String string = new String();
        if (daysUntilAlert > 1) {
            string = mContext.getString(R.string.alert_first_part) + " " + daysUntilAlert + " " + mContext.getString(R.string.alert_multi_days_until);
        } else if (daysUntilAlert == 1) {
            string = mContext.getString(R.string.alert_one_day_until);
        } else if (daysUntilAlert == 0){
            string = mContext.getString(R.string.alert_today);
        } else if (daysUntilAlert == -1) {
            string = mContext.getString(R.string.alert_one_day_ago);
        } else {
            string =  daysUntilAlert * -1 +  " " + mContext.getString(R.string.alert_multi_days_ago);
        }
        return string;
    }


    private String getStringForTvInterval(Item item) {
        int interval = item.getmInterval();
        String string = new String();
        if (interval == -1) {
            string = mContext.getString(R.string.interval_first_part) + " " + mContext.getString(R.string.interval_not_set);
        } else if (interval == 1) {
            string = mContext.getString(R.string.interval_first_part) + " " + interval + " " + mContext.getString(R.string.interval_one_day);
        } else {
            string = mContext.getString(R.string.interval_first_part) + " " + interval + " " + mContext.getString(R.string.interval_multi_days);
        }
        return string;
    }

}