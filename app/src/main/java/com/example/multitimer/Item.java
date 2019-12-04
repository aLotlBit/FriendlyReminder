package com.example.multitimer;

import java.util.Comparator;

public class Item {

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

        // Constructor
    public Item(Integer mID, String mTitle, long mMillisStart, long mMillisEnd , int mHourOfDay, int mMinuteOfDay, int mInterval, int mAlertActive){
        this.mID = mID;
        this.mTitle = mTitle;
        this.mMillisStart = mMillisStart;
        this.mMillisEnd = mMillisEnd;
        this.mInterval = mInterval;
        this.mHourOfDay = mHourOfDay;
        this.mMinuteOfDay = mMinuteOfDay;
        this.mAlertActive = mAlertActive;
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

        public void setmMillisEnd(long millisUntil) {
        this.mMillisEnd = System.currentTimeMillis() + millisUntil;
            }

        public void setHourOfDay(int mHourOfDay) {
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

        public Integer getDaysLeft() {
             // calculation for days:
            //int daysLeft = (int) ((mMillisEnd - System.currentTimeMillis()) / 86400000L);
            // calculation for minutes for testing
            if (mMillisEnd != -1) {
                Integer daysLeft = (int) ((mMillisEnd - System.currentTimeMillis()) / 1000 / 60);
                return daysLeft;
            } else {
                Integer daysLeft = -1;
                return daysLeft;
            }


        }

        public long daysToMillis(int days) {
        //days for later
        // long millis = (long) days *  86400000L;
        //minutes for testing
        long millis = (long) days * 60 * 1000;
        return millis;
        }

    // calculates minutes for testing purposes
        public String getDaysPassed (){
            long millisCurrent = System.currentTimeMillis();
            long diff = millisCurrent - mMillisStart;
            long min =  diff / 1000 / 60;

        return String.valueOf(min);
    }


    // Comparators
     public static class CompDaysUntilAlert implements Comparator<Item> {
        @Override
        public int compare(Item arg0, Item arg1) {
            return (int) arg0.mMillisEnd - (int) arg1.mMillisEnd;
        }
    }

    public static class CompDaysPassed implements Comparator<Item> {
        @Override
        public int compare(Item arg0, Item arg1) {
            return (int) arg0.mMillisStart- (int) arg1.mMillisStart;
        }
    }





    /*
    @Override
    public int compareTo(Item item) {
        return Integer.compare(mAlertActive, item.mAlertActive);
    }
    */
}
