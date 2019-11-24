package com.example.multitimer;

public class Item {

        // Store the Title of the item
        private String mTitle;
        // Store the millis when item was created
        private long mMillisStart;
        // Store the millis of
        private long mMillisEnd;

        private int mInterval;

        private int mHourOfDay;

        private int mMinuteOfDay;

        // Constructor that is used to create an instance of the Movie object
    public Item(String mTitle, long mMillisStart, long mMillisEnd , int mHourOfDay, int mMinuteOfDay, int mInterval){
        this.mTitle = mTitle;
        this.mMillisStart = mMillisStart;
        this.mMillisEnd = mMillisEnd;
        this.mInterval = mInterval;
        this.mHourOfDay = mHourOfDay;
        this.mMinuteOfDay = mMinuteOfDay;
    }

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
        return mMillisEnd;
    }

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

        public int getDaysLeft() {
             // calculation for days:
            //int daysLeft = (int) ((mMillisEnd - System.currentTimeMillis()) / 86400000L);
            // calculation for minutes for testing
            int daysLeft = (int) ((mMillisEnd - System.currentTimeMillis()) / 1000 / 60);

            return daysLeft;
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

}
