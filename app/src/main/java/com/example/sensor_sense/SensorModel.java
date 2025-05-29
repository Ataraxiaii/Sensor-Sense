package com.example.sensor_sense;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorModel implements Parcelable {
    private int id;
    private String title;
    private int type; // 对应Sensor.TYPE_*

    // Constructor
    public SensorModel(int id, String title, int type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    // Parcelable实现
    protected SensorModel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        type = in.readInt();
    }

    public static final Creator<SensorModel> CREATOR = new Creator<SensorModel>() {
        @Override
        public SensorModel createFromParcel(Parcel in) {
            return new SensorModel(in);
        }

        @Override
        public SensorModel[] newArray(int size) {
            return new SensorModel[size];
        }
    };

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getType() { return type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(type);
    }
}