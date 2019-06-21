package com.example.firstmap.Weather;

import com.google.gson.annotations.SerializedName;

public class weather {

    @SerializedName("main")
    String mMain;

    @SerializedName("description")
    String mDescription;

    public String getmDescription() {
        return mDescription;
    }

    public void setmMain(String mMain) {
        this.mMain = mMain;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmMain() {
        return mMain;
    }
}