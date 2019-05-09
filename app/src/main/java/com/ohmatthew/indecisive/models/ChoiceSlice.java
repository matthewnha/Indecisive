package com.ohmatthew.indecisive.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChoiceSlice implements Parcelable {
    private String name;
    private int color;

    private ChoiceSlice(Parcel in) {
        name = in.readString();
        color = in.readInt();
    }

    public ChoiceSlice(String name, int color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<ChoiceSlice> CREATOR = new Parcelable.Creator<ChoiceSlice>() {
        @Override
        public ChoiceSlice createFromParcel(Parcel source) {
            return new ChoiceSlice(source);
        }

        @Override
        public ChoiceSlice[] newArray(int size) {
            return new ChoiceSlice[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
