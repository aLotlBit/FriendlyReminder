package com.example.multitimer;

import android.util.Log;

import java.util.Calendar;
import java.util.Comparator;

public class Item {

        private static int DEFAULT_HOUR_OF_DAY = 12;
        private static int DEFAULT_MINUTE_OF_DAY = 0;

        private int mID;
        // Store the Title of the item
        private String mTitle;
        // Store the millis when item was created
        private long mMillisStart;
        // Store the millis of
        private long mMillisEnd;

        private int mInterval;

        private int mHourOfDay;

        private int mMinuteOfDay;

        private int mAlertActive;

        private boolean mExpanded;

    public Item(Integer mID, String mTitle, long mMillisStart, long mMillisEnd , int mInterval, int mAlertActive) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mMillisStart = mMillisStart;
        this.mMillisEnd = mMillisEnd;
        this.mInterval = mInterval;
        this.mAlertActive = mAlertActive;
        this.mExpanded = false;

        Calendar calendar = Calendar.getInstance();
        if (mMillisEnd != -1) {
            calendar.setTimeInMillis(mMillisEnd);
            this.mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            this.mMinuteOfDay = calendar.get(Calendar.MINUTE);
        } else {
            this.mHourOfDay = DEFAULT_HOUR_OF_DAY;
            this.mMinuteOfDay = DEFAULT_MINUTE_OF_DAY;
        }
    }

        public int getmID() {return mID; }

        public void setmID() {this.mID = mID; }

        public String getmTitle() {
        return mTitle;
    }

        public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

        public long getmMillisStart() {
        return mMillisStart;
    }

        public void setmMillisStart(long mMillisStart) {
        this.mMillisStart = mMillisStart;
    }

        public long getmMillisEnd() {
        return mMillisEnd; }

        public void setmMillisEnd(long millisEnd) {
        this.mMillisEnd = millisEnd;
            }

        public void setmHourOfDay(int mHourOfDay) {
        this.mHourOfDay = mHourOfDay;
        }

        public int getmHourOfDay() {
        return mHourOfDay;
        }

        public void setmMinuteOfDay(int mMinuteOfDay) {
        this.mMinuteOfDay = mMinuteOfDay;
    }

        public int getmMinuteOfDay() {
        return mMinuteOfDay;
    }

        public void setmInterval(int mInterval) {
        this.mInterval = mInterval;
        }

        public int getmInterval() {
        return mInterval;
        }

        public void setmAlertActive(int mAlertActive) {this.mAlertActive = mAlertActive; }

        public boolean getmAlertActive() {
        boolean active;
        if (mAlertActive == 0) {
                active = false;
            } else {
                active = true;
            }
                return active;
        }

        public void setmExpanded(boolean mExpanded)  {this.mExpanded = mExpanded; };
        public boolean getmExpanded() {return mExpanded; }

        public Integer getDaysLeft() {
            if (mMillisEnd != -1) {
                Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(mMillisEnd);
                int day_end = calendar.get(Calendar.DAY_OF_YEAR);

                calendar.setTimeInMillis(System.currentTimeMillis());
                int day_now = calendar.get(Calendar.DAY_OF_YEAR);

                return day_end - day_now;
            }else {
                return -1;
            }

         /*
            // calculation for minutes for testing
            if (mMillisEnd != -1) {
                Integer daysLeft = (int) ((mMillisEnd - System.currentTimeMillis()) / 1000 / 60);
                return daysLeft;
            } else {
                return -1;
            }

          */
        }


        static long daysToMillis(int days) {
        //days for later
        // long millis = (long) days *  86400000L;
        //minutes for testing
        long millis = (long) days * 60 * 1000;
        return millis;
        }

        public String getDaysPassed (){
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(mMillisStart);
            int day_start = calendar.get(Calendar.DAY_OF_YEAR);

            calendar.setTimeInMillis(System.currentTimeMillis());
            int day_now = calendar.get(Calendar.DAY_OF_YEAR);

            return String.valueOf(day_now - day_start);


            /*minutes for Testing
            long millisCurrent = System.currentTimeMillis();
            long diff = millisCurrent - mMillisStart;
            long min =  diff / 1000 / 60;

             */
    }



    public long calcMillisEnd(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        calendar.set(Calendar.HOUR_OF_DAY, getmHourOfDay());
        calendar.set(Calendar.MINUTE, getmMinuteOfDay());
        calendar.set(Calendar.SECOND, 0);

       return calendar.getTimeInMillis();
    }


    // Comparators
     public static class CompDaysUntilAlert implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            //different sort orders for active alerts and past alerts
            if (i1.mMillisEnd - System.currentTimeMillis() >= 0 && i2.mMillisEnd - System.currentTimeMillis() >= 0) {
                return (int) i1.mMillisEnd - (int) i2.mMillisEnd;
            }
            return (int) i2.mMillisEnd - (int) i1.mMillisEnd;
        }
    }


    public static class CompDaysPassed implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            return (int) i2.mMillisStart- (int) i1.mMillisStart;
        }
    }

    public static class CompAlertActive implements Comparator<Item> {
        @Override
        public int compare(Item i1, Item i2) {
            int active1;
            int active2;
            if (i1.getmAlertActive()) {active1 = 1;}
            else {active1 = 0;}
            if (i2.getmAlertActive()) {active2 = 1;}
            else {active2 = 0;}

            return (int) active1 - active2;
        }
    }


}
