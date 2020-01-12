package com.example.multitimer;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {


    private Context mContext;
    private List<Item> itemsList;

    public ItemAdapter(@NonNull Context context, ArrayList<Item> list) {
        super(context, 0, list);
        this.mContext = context;
        itemsList = list;
    }

    @Override
    public Item getItem(int position) {
        return itemsList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Item currentItem = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item_layout, null, true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_daysPassed = convertView.findViewById(R.id.tv_days_passed);
            holder.tv_daysUntilAlert = convertView.findViewById(R.id.tv_days_left);
            holder.tv_timeOfDay = convertView.findViewById(R.id.tv_time_of_day);
            holder.tv_daysUntilAlertShown = convertView.findViewById(R.id.tv_days_left_shown);
            holder.tv_timeOfDayShown = convertView.findViewById(R.id.tv_time_of_day_shown);


            holder.tv_title.setText(currentItem.getmTitle());
            holder.tv_daysPassed.setText(currentItem.getDaysPassed());

         //   holder.tv_daysUntilAlertShown.setText(getStringForTvDaysUntilAlert(currentItem));


            holder.tv_daysUntilAlert.setText(getStringForTvDaysUntilAlert(currentItem));
            holder.tv_timeOfDay.setText(getStringForTvTimeOfDay(currentItem));
            holder.tv_interval = convertView.findViewById(R.id.tv_interval);
            holder.cb_alertActive = convertView.findViewById(R.id.cb_alert_active);

            holder.groupExpanded = convertView.findViewById(R.id.expanded);
            holder.ivAlarm = convertView.findViewById(R.id.iv_alarm);
            holder.ivArrow = convertView.findViewById(R.id.iv_arrow);
            holder.tv_delete = convertView.findViewById(R.id.tv_delete);
            holder.tv_restart = convertView.findViewById(R.id.tv_reset);

            holder.iv_eye = convertView.findViewById(R.id.iv_eye);
            holder.iv_day = convertView.findViewById(R.id.iv_day);
            holder.iv_time_of_day = convertView.findViewById(R.id.iv_time_of_day);
            holder.iv_interval = convertView.findViewById(R.id.iv_interval);
            holder.iv_delete = convertView.findViewById(R.id.iv_delete);
            holder.iv_days_passed = convertView.findViewById(R.id.iv_days_passed);




        /*
        //TODO ALertStatus
            if (currentItem.getmAlertStatus() == 1) {
                holder.ivAlarm.setImageResource(R.drawable.alert);
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextRegular));
                holder.tv_timeOfDayShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextRegular));
            } else if (currentItem.getmMillisEnd() >= System.currentTimeMillis()){
                holder.ivAlarm.setImageResource(R.drawable.alert_off);
                //TODO set to textStyle
                  holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
                  holder.tv_timeOfDayShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
            } else {
                holder.ivAlarm.setImageResource(R.drawable.alert_active);
            }

            //TODO ALertStatus

            //only show timeOfDay in nonExpanded view when there is an upcoming alert
            if (currentItem.getmMillisEnd() < System.currentTimeMillis()) {
                holder.tv_timeOfDayShown.setVisibility(View.INVISIBLE);
            } else {
                holder.tv_timeOfDayShown.setText(" " + getStringForTvTimeOfDay(currentItem));
                holder.tv_timeOfDayShown.setVisibility(View.VISIBLE);
            }

         */



        holder.tv_interval.setText(getStringForTvInterval(currentItem));
            holder.cb_alertActive.setTag(position);
            holder.cb_alertActive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    toggleAlertStatus(holder, currentItem);
                }
            });

            holder.tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTitle(view, position);
                    }
                }
            });

            holder.tv_daysUntilAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeDaysLeft(view, position);
                    }
                }
            });

            holder.tv_timeOfDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTimeOfDay(view, position);
                    }
                }
            });

            holder.tv_interval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeInterval(view, position);
                    }
                }
            });

            holder.tv_restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).restartAlert(currentItem.getmID());
                    }
                }
            });

            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).showDialogConfirmation(currentItem, 0);
                    }
                }
            });

            holder.iv_eye.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleAlertStatus(holder, currentItem);
                }
            });

            holder.iv_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeDaysLeft(view, position);
                }
            });

            holder.iv_time_of_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeTimeOfDay(view, position);
                }
            });

            holder.iv_interval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).changeInterval(view, position);
                }
            });

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).showDialogConfirmation(currentItem, 0);
                    }
                }
            });

            holder.ivAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        toggleAlertStatus(holder, currentItem);
                }
            });

            holder.tv_daysUntilAlertShown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleAlertStatus(holder, currentItem);
                }
            });


            holder.tv_timeOfDayShown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleAlertStatus(holder, currentItem);
                }
            });


            holder.iv_days_passed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mContext).showDialogNumberPicker(currentItem, 2);
                }
            });





//            holder.tv_title.setBackgroundColor(getColorByAttributeId(mContext, android.R.attr.selectableItemBackground));
            //must be set after onClickListeners to make TextViews unckliable after startup
              holder.tv_title.setClickable(false);
      //      holder.tv_daysPassed.setClickable(false);
     //       holder.tv_daysUntilAlert.setClickable(false);
     //       holder.tv_timeOfDay.setClickable(false);


            if (!currentItem.getmExpanded()) {
                holder.groupExpanded.setVisibility(View.GONE);
                holder.iv_eye.setVisibility(View.GONE);
                holder.cb_alertActive.setVisibility(View.GONE);
                holder.ivArrow.setImageResource((R.drawable.arrow_down));

                //remove click effect so effect doesn't show while item is getting expanded
                holder.tv_title.setBackgroundResource(0);
                holder.tv_title.setClickable(false);

                if (currentItem.getmAlertStatus() == 4) {
                    convertView.setBackgroundColor(getColorByAttributeId(mContext, R.attr.colorBackgroundSelected));
                } else {
                    convertView.setBackgroundColor(getColorByAttributeId(mContext, R.attr.colorBackground));
                }

            } else {
                holder.groupExpanded.setVisibility(View.VISIBLE);
                holder.ivArrow.setImageResource((R.drawable.arrow_up));

                //Set backgroundResource to get click effect
                TypedValue typedValue = new TypedValue();
                mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
                holder.tv_title.setBackgroundResource(typedValue.resourceId);
                holder.tv_title.setClickable(true);

                if (currentItem.getmAlertStatus() == 4) {
                    holder.cb_alertActive.setVisibility(View.GONE);
                    holder.iv_eye.setVisibility(View.VISIBLE);
                } else {
                    holder.cb_alertActive.setVisibility(View.VISIBLE);
                    holder.iv_eye.setVisibility(View.GONE);
                }
                convertView.setBackgroundColor(getColorByAttributeId(mContext, R.attr.colorBackgroundSelected));
            }


        switch (currentItem.getmAlertStatus()){
            case 0:  //alert not set
                holder.ivAlarm.setImageResource(R.drawable.alert_plus);
                holder.tv_daysUntilAlertShown.setText(R.string.alert_not_set);
                holder.tv_timeOfDayShown.setVisibility(View.INVISIBLE);
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
                holder.cb_alertActive.setChecked(false);
                break;
            case 1: // alert was ringing and marked seen
                holder.ivAlarm.setImageResource(R.drawable.alert_rang);
                holder.tv_daysUntilAlertShown.setText(getStringForTvDaysUntilAlertShown(currentItem));
                holder.tv_timeOfDayShown.setVisibility(View.INVISIBLE);
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
                holder.cb_alertActive.setChecked(false);
                break;
            case 2: // alert is disabled
                holder.ivAlarm.setImageResource(R.drawable.alert_off);
                holder.tv_daysUntilAlertShown.setText(getStringForTvDaysUntilAlertShown(currentItem));
                holder.tv_timeOfDayShown.setVisibility(View.VISIBLE);
                holder.tv_timeOfDayShown.setText(getStringForTvTimeOfDayShown(currentItem));
                holder.tv_timeOfDayShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextGrayed));
                holder.cb_alertActive.setChecked(false);
                break;
            case 3: // alert is enabled
                holder.ivAlarm.setImageResource(R.drawable.alert);
                holder.tv_daysUntilAlertShown.setText(getStringForTvDaysUntilAlertShown(currentItem));
                holder.tv_timeOfDayShown.setVisibility(View.VISIBLE);
                holder.tv_timeOfDayShown.setText(getStringForTvTimeOfDayShown(currentItem));
                holder.tv_timeOfDayShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextRegular));
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextRegular));
                holder.cb_alertActive.setChecked(true);
                break;
            case 4: // was ringing and was not marked seen
                holder.ivAlarm.setImageResource(R.drawable.alert_rang);
                holder.tv_daysUntilAlertShown.setText(getStringForTvDaysUntilAlertShown(currentItem));
                holder.tv_timeOfDayShown.setVisibility(View.INVISIBLE);
                holder.tv_daysUntilAlertShown.setTextColor(getColorByAttributeId(mContext, R.attr.colorTextRegular));
                //TODO change to different color
                holder.cb_alertActive.setChecked(false);
                break;
        }

        return convertView;
    }

    @ColorInt
    static int getColorByAttributeId(Context context, @AttrRes int attrIdForColor){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrIdForColor, typedValue, true);
        return typedValue.data;
    }


    private void toggleAlertStatus(ViewHolder holder, Item currentItem) {
        //TODO check if checkboxes work correctly without these lines
        //Integer pos = (Integer) holder.cb_alertActive.getTag();
        //Item item = itemsList.get(pos);

        if (currentItem.getmAlertStatus() == 3) {   // 3 = alert is active
            currentItem.setmAlertStatus(2);     // 2 = alert is disabled
            SharedPreferencesHelper.setInt(mContext, "alert_status_" + currentItem.getmID(), 2);
            ((MainActivity) mContext).cancelAlert(currentItem.getmTitle(), currentItem.getmID());
            notifyDataSetChanged();
        } else if (currentItem.getmAlertStatus() == 4) { // alert is not seen
            currentItem.setmAlertStatus(1);     // 1 = alert is seen
            SharedPreferencesHelper.setInt(mContext, "alert_status_" + currentItem.getmID(), 1);
            currentItem.setmExpanded(false);
            notifyDataSetChanged();
            //setAlert returns true when millisEnd is not in past and alert is set
            // alertStatus is set by setAlarm method
        } else if (!((MainActivity) mContext).setAlert(currentItem, currentItem.getmMillisEnd())) {
            holder.cb_alertActive.setChecked(false);
            if (currentItem.getmInterval() == -1) {
                ((MainActivity) mContext).showDialogNumberPicker(currentItem, 0);
            } else {
                ((MainActivity) mContext).restartAlert(currentItem.getmID());
            }
        }
    }


    //only loads expanded items when convertView != null; BUT expanding after launch doesn't work.
    /*
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final Item currentItem = getItem(position);

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item_layout, null, true);

            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_daysPassed = convertView.findViewById(R.id.tv_days_passed);
            holder.tv_daysUntilAlert = convertView.findViewById(R.id.tv_days_left);
            holder.tv_timeOfDay = convertView.findViewById(R.id.tv_time_of_day);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.tv_interval = convertView.findViewById(R.id.tv_interval);
            holder.cb_alertActive = convertView.findViewById(R.id.cb_alert_active);
            holder.tv_restart = convertView.findViewById(R.id.tv_reset);
            holder.groupExpanded = convertView.findViewById(R.id.expanded);
            holder.ivAlarm = convertView.findViewById(R.id.iv_alarm);
            holder.ivArrow = convertView.findViewById(R.id.iv_arrow);
            holder.tv_delete = convertView.findViewById(R.id.tv_delete);

            holder.tv_interval.setText(getStringForTvInterval(currentItem));
            holder.cb_alertActive.setChecked(currentItem.getmAlertActive());
            //     holder.cb_alertActive.setTag(R.integer.checkboxbview, convertView);
            holder.cb_alertActive.setTag(position);
            holder.cb_alertActive.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Integer pos = (Integer) holder.cb_alertActive.getTag();
                    Item item = itemsList.get(pos);

                        if (item.getmAlertActive()) {
                        item.setmAlertActive(0);
                        SharedPreferencesHelper.setInt(mContext, "alert_active_" + currentItem.getmID(), 0);
                        ((MainActivity) mContext).cancelAlert(currentItem.getmTitle(), currentItem.getmID());


                    } else if (item.getmMillisEnd() - System.currentTimeMillis() > 0) {
                        item.setmAlertActive(1);
                        SharedPreferencesHelper.setInt(mContext, "alert_active_" + currentItem.getmID(), 1);
                        ((MainActivity) mContext).setAlert(currentItem.getmMillisEnd(), currentItem.getmTitle(), currentItem.getmID());
                    } else {
                            holder.cb_alertActive.setChecked(false);
                            ((MainActivity) mContext).showDialogNumberPicker(item, true);
                        }
                }

            });

            holder.tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTitle(view, position);
                    }
                }
            });

            holder.tv_daysUntilAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeDaysLeft(view, position);
                    }
                }
            });

            holder.tv_timeOfDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeTimeOfDay(view, position);
                    }
                }
            });

            holder.tv_interval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).changeInterval(view, position);
                    }
                }
            });

            holder.tv_restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).restartAlert(currentItem.getmID());
                    }
                }
            });

            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //access method in mainActivity from adapter
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).deleteItem(currentItem);
                    }
                }
            });



            //must be set after onClickListeners to make TextViews unckliable after startup
            holder.tv_title.setClickable(false);
            holder.tv_daysPassed.setClickable(false);
            holder.tv_daysUntilAlert.setClickable(false);
            holder.tv_timeOfDay.setClickable(false);



            if (!currentItem.getmExpanded()) {
                holder.groupExpanded.setVisibility(View.GONE);
                holder.tv_daysUntilAlert.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_small));
                holder.tv_timeOfDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_small));
                //TODO why does is textsize differen when set with xml?
                holder.tv_interval.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_medium));
                holder.ivAlarm.setImageResource(R.drawable.notification);
                holder.ivArrow.setImageResource((R.drawable.arrow_down));
                holder.tv_daysPassed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timer, 0, 0, 0);
                holder.tv_title.setClickable(false);
                holder.tv_daysUntilAlert.setClickable(false);
                holder.tv_timeOfDay.setClickable(false);
                convertView.setBackgroundColor(0xffffffff);

            } else {
                holder.groupExpanded.setVisibility(View.VISIBLE);
                holder.tv_daysUntilAlert.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_medium));
                holder.tv_timeOfDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_medium));
                holder.tv_interval.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContext.getResources().getDimension(R.dimen.font_medium));
                holder.ivAlarm.setImageResource(R.drawable.edit);
                holder.ivArrow.setImageResource((R.drawable.arrow_up));
                holder.tv_daysPassed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.refresh, 0, 0, 0);
                holder.tv_title.setClickable(true);
                holder.tv_daysUntilAlert.setClickable(true);
                holder.tv_timeOfDay.setClickable(true);

                convertView.setBackgroundColor(0xffcccccc);
            }

        }




        holder.tv_title.setText(currentItem.getmTitle());
        holder.tv_daysPassed.setText(currentItem.getDaysPassed());
        holder.tv_daysUntilAlert.setText(getStringForTvDaysUntilAlert(currentItem));

        return convertView;
    }

     */

    private String getStringForTvDaysUntilAlertShown(Item item) {
        int daysUntilAlert = item.getDaysLeft();
        String string = new String();
        if (daysUntilAlert > 1) {
            string = mContext.getString(R.string.alert_first_part) + " " + daysUntilAlert + " " + mContext.getString(R.string.alert_multi_days_until);
        } else if (daysUntilAlert == 1) {
            string = mContext.getString(R.string.alert_one_day_until);
        } else if (daysUntilAlert == 0){
            string = mContext.getString(R.string.alert_today);
        } else if (daysUntilAlert == -1) {
            if (item.getmMillisEnd() != -1) {
                string = mContext.getString(R.string.alert_one_day_ago);
            } else {
                string = mContext.getString(R.string.alert_not_set);

            }
        } else {
            string =  daysUntilAlert * -1 +  " " + mContext.getString(R.string.alert_multi_days_ago);
        }
        return string;
    }


    private String getStringForTvDaysUntilAlert(Item item) {
        int daysUntilAlert = item.getDaysLeft();
        String string = new String();
        if (daysUntilAlert > 1) {
            string = daysUntilAlert + " " + mContext.getString(R.string.alert_multi_days_until);
        } else if (daysUntilAlert == 1) {
            string = mContext.getString(R.string.alert_one_day_until);
        } else if (daysUntilAlert == 0){
            string = mContext.getString(R.string.alert_today);
        } else if (daysUntilAlert == -1) {
            if (item.getmMillisEnd() != -1) {
                string = mContext.getString(R.string.alert_one_day_ago);
            } else {
                string = mContext.getString(R.string.alert_not_set);
            }
        } else {
            string =  daysUntilAlert * -1 +  " " + mContext.getString(R.string.alert_multi_days_ago);
        }
        return string;
    }




    private String getStringForTvTimeOfDay(Item item) {
        String hour = String.valueOf(item.getmHourOfDay());
        String min = String.valueOf(item.getmMinuteOfDay());
        if (hour.length() == 1) hour = "0" + hour;
        if (min.length() == 1) min = "0" + min;

        return hour + ":" + min;
    }

    private String getStringForTvTimeOfDayShown(Item item) {
        String string = " " + mContext.getString(R.string.alert_time_of_day_first_part) + " " +getStringForTvTimeOfDay(item);
        return string;
    }



    private String getStringForTvInterval(Item item) {
        int interval = item.getmInterval();
        String string = new String();
        if (interval == -1) {
            string = mContext.getString(R.string.interval_not_set);
        } else if (interval == 1) {
            string = interval + " " + mContext.getString(R.string.interval_one_day);
        } else {
            string = interval + " " + mContext.getString(R.string.interval_multi_days);
        }
        return string;
    }


}

class ViewHolder {
    protected TextView tv_title;
    protected TextView tv_daysPassed;
    protected TextView tv_daysUntilAlertShown;
    protected TextView tv_timeOfDayShown;

    protected TextView tv_daysUntilAlert;
    protected TextView tv_timeOfDay;
    protected TextView tv_interval;
    protected CheckBox cb_alertActive;

    protected ImageView ivAlarm;
    protected ImageView ivArrow;
    protected View groupExpanded;
    protected TextView tv_delete;
    protected TextView tv_restart;

    protected ImageView iv_eye;
    protected ImageView iv_day;
    protected ImageView iv_time_of_day;
    protected ImageView iv_interval;
    protected ImageView iv_delete;
    protected ImageView iv_days_passed;


}




    /*
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

     */
