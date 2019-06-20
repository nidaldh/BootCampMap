package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class WeatherMain {
    @SerializedName("mTemp")
    double mTemp;
    @SerializedName("mTempMin")
    double mTempMin;
    @SerializedName("mTempMax")
    double mTempMax;
}
