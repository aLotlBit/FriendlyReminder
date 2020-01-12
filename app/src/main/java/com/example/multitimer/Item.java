package com.example.multitimer;

import android.util.Log;


import androidx.annotation.NonNull;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Item implements Comparable<Item>{

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

        private int mAlertStatus;

        private boolean mExpanded;

    public Item(Integer mID, String mTitle, long mMillisStart, long mMillisEnd , int mInterval, int mAlertStatus) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mMillisStart = mMillisStart;
        this.mMillisEnd = mMillisEnd;
        this.mInterval = mInterval;
        this.mAlertStatus = mAlertStatus;
        this.mExpanded = false;

        if (mMillisEnd != -1) {
            LocalDateTime timeOfAlarm = Instant.ofEpochMilli(mMillisEnd)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            this.mHourOfDay = timeOfAlarm.getHour();
            this.mMinuteOfDay = timeOfAlarm.getMinute();
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

        public Integer getmInterval() {
        return mInterval;
        }

        public void setmAlertStatus(int mAlertStatus) {this.mAlertStatus = mAlertStatus; }

        public int getmAlertStatus() { return mAlertStatus; }

        public void setmExpanded(boolean mExpanded)  {this.mExpanded = mExpanded; };
        public boolean getmExpanded() {return mExpanded; }



        public Integer getDaysLeft() {
           if (mMillisEnd != -1) {
               LocalDate now = LocalDate.now();
               LocalDate dayOfAlarm = Instant.ofEpochMilli(mMillisEnd)
                       .atZone(ZoneId.systemDefault())
                       .toLocalDate();
               return (int) ChronoUnit.DAYS.between(now, dayOfAlarm);
           } else {
               return -1;
           }
        }

        static long daysToMillis(int days) {
        long millis = (long) days * 86400000L;
        return millis;
        }


        public String getDaysPassed (){
            LocalDate now = LocalDate.now();
            LocalDate start = Instant.ofEpochMilli(mMillisStart)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return String.valueOf(ChronoUnit.DAYS.between(start, now));
        }



        public long calcMillisEnd(int daysToAdd) {
            LocalDateTime date = LocalDateTime.now().plusDays(daysToAdd).withHour(mHourOfDay).withMinute(mMinuteOfDay).withSecond(0);
            return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        @Override
        public int compareTo(@NonNull Item i){
            return 1;
        }

        // Comparators
         public static class CompDaysUntilAlert implements Comparator<Item> {
            @Override
            public int compare(Item i1, Item i2) {
                if (true) {
                    if (i1.getmMillisEnd() - i2.getmMillisEnd() < 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    if (i2.getmMillisEnd() - i1.getmMillisEnd() < 0) {
                        return -1;
                    } else {
                        return 1;
                    }

                }
            }
        }


        public static class CompDaysPassed implements Comparator<Item> {
            @Override
            public int compare(Item i1, Item i2) {
                if (i1.getmMillisStart() - i2.getmMillisStart() < 0 ) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

        public static class CompAlertStatus implements Comparator<Item> {
            @Override
            public int compare(Item i1, Item i2) {
                return i2.getmAlertStatus() - i1.getmAlertStatus();
            }
        }

        public static class CompAlphabet implements Comparator<Item> {
            @Override
            public int compare(Item i1, Item i2) {
                return i1.getmTitle().compareToIgnoreCase(i2.getmTitle());
            }
        }


}
