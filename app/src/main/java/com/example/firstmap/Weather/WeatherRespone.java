package com.example.firstmap.Weather;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherRespone {

    @SerializedName("weather")
    List<weather> mWeather;

    @SerializedName("main")
    WeatherMain mMain;

    public List<weather> getmWeather() {
        return mWeather;
    }

    public void setmWeather(List<weather> mWeather) {
        this.mWeather = mWeather;
    }

    public WeatherMain getmMain() {
        return mMain;
    }

    public void setmMain(WeatherMain mMain) {
        this.mMain = mMain;
    }
}