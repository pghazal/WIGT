package com.punchlag.wigt.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

    private int hour;
    private int minute;

    public Alarm(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public Alarm(Parcel in) {
        this.hour = (Integer) in.readValue(Integer.class.getClassLoader());
        this.minute = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(hour);
        dest.writeValue(minute);
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public String toString() {
        return Alarm.class.getSimpleName() + "# " + hour + ":" + minute;
    }
}
