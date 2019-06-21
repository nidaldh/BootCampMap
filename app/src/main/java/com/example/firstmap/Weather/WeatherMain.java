package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class WeatherMain {

    @SerializedName("temp")
    double mTemp;

    @SerializedName("temp_min")
    double mTemp_min;

    @SerializedName("temp_max")
    double mTemp_max;

    public void setmTemp(double mTemp) {
        this.mTemp = mTemp;
    }

    public void setmTemp_min(double mTemp_min) {
        this.mTemp_min = mTemp_min;
    }

    public void setmTemp_max(double mTemp_max) {
        this.mTemp_max = mTemp_max;
    }

    public double getmTemp_min() {
        return mTemp_min;
    }

    public double getmTemp_max() {
        return mTemp_max;
    }

    public double getmTemp() {
        return mTemp;
    }
}