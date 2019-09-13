package com.salman.tourmateapp.retrofit;

import com.salman.tourmateapp.model.weather.WeatherForecastResult;
import com.salman.tourmateapp.model.weather.WeatherResult;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WeatherService {


    @GET
    Call<WeatherResult> getCurrentWeatherResponse(@Url String url);

    @GET("forecast")
    Observable<WeatherForecastResult> getForeCastWeather(@Query("lat") String lat,
                                                         @Query("lon") String lon,
                                                         @Query("appid") String appid,
                                                         @Query("units") String unit);
}
