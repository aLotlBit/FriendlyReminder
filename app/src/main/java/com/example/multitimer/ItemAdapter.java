package com.example.multitimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {


        private Context mContext;
        private List<Item> itemsList = new ArrayList<>();

        public ItemAdapter(@NonNull Context context, ArrayList<Item> list) {
            super(context, 0 , list);
            mContext = context;
            itemsList = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItem = convertView;
            if(listItem == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                listItem = vi.inflate(R.layout.list_item_layout, null);

            }
            Item currentItem = itemsList.get(position);

            if (currentItem != null) {


                //   TextView title = (TextView) listItem.findViewById(R.id.title);
                TextView title = (TextView) listItem.findViewById(R.id.title);
                TextView daysPassed = (TextView) listItem.findViewById(R.id.time_since_start);
                TextView daysUntil = (TextView) listItem.findViewById(R.id.time_to_reminder);
                if (title != null) {
                    title.setText(currentItem.getmTitle());
                }
                if (daysPassed != null) {
                    daysPassed.setText(currentItem.getDaysPassed());
                }
                if (daysUntil != null) {
                    daysUntil.setText(String.valueOf(currentItem.getDaysLeft()));
                }
            }
            return listItem;
        }


}


