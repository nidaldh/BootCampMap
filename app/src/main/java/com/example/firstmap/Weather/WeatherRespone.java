package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherRespone {
    @SerializedName("Weather")
    List<weather> mWeather;
    @SerializedName("main")
    WeatherMain mMain;
}
