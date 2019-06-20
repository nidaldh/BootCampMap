package com.example.firstmap.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("/data/2.5/weather")
    Call<WeatherRespone> get(@Query("APPID") String token,@Query("lat") String latitude ,@Query("lon") String longitude);
}
