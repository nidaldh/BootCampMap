package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class weather {
    @SerializedName("Main")
    String mMain;
    @SerializedName("Description")
    String mDescription;
}
