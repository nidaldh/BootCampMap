package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class WeatherMain {
    @SerializedName("Temp")
    double mTemp;
    @SerializedName("TempMin")
    double mTempMin;
    @SerializedName("TempMax")
    double mTempMax;
}
