package com.example.firstmap.Weather;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    // we set query to put the variable in the url in get method
//    api.openweathermap.org/data/2.5/weather?lat=35&lon=139


    @GET("/data/2.5/weather")
    Call<WeatherRespone> get(@Query("APPID") String token, @Query("lat") String latitude, @Query("lon") String longtitude);
}