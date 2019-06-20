package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class weather {
    @SerializedName("mMain")
    String mMain;
    @SerializedName("mDescription")
    String mDescription;
}
