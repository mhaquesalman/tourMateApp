package com.salman.tourmateapp.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.WeatherForcastAdapter;
import com.salman.tourmateapp.model.weather.WeatherForecastResult;
import com.salman.tourmateapp.model.weather.WeatherResult;
import com.salman.tourmateapp.retrofit.RetrofitClient;
import com.salman.tourmateapp.retrofit.WeatherService;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    AppCompatImageView imageView;
    TextView windTV, tempTV, humidityTV;
    RecyclerView weatherRV;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    public static Location location;
    public static String lat;
    public static String lon;
    public static String units = "metric";
    ProgressDialog progressDialog;
    WeatherService weatherService;
    CompositeDisposable compositeDisposable;
    static WeatherFragment instance;
    public static WeatherFragment getInstance() {
        if (instance == null) {
            instance = new WeatherFragment();
        }
        return instance;
    }

    public WeatherFragment() {
        Retrofit retrofit = RetrofitClient.getRetrofit();
        weatherService = retrofit.create(WeatherService.class);
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        imageView = view.findViewById(R.id.imageView);
        windTV = view.findViewById(R.id.windTV);
        tempTV = view.findViewById(R.id.tempTV);
        humidityTV = view.findViewById(R.id.humidityTV);

        weatherRV = view.findViewById(R.id.weatherRV);
        weatherRV.setHasFixedSize(true);
        weatherRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        checkPermission();
        progressDialog.show();
        return view;
    }

    public void checkPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallback();

                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    public void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                //lat = String.format("%.2f", location.getLatitude());
                //lon = String.format("%.2f", location.getLongitude());
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
                Log.d(TAG, "Location: " + lat + " / " + lon);
                getWeatherInformation();
                getForecastInformation();
            }
        };
    }

    // 23.74, 90.37
    public void getWeatherInformation() {
        //progressDialog.show();
        String urlString = String.format("weather?lat=%s&lon=%s&appid=%s&units=%s", lat, lon, RetrofitClient.APP_ID, units);
        Call<WeatherResult> call = weatherService.getCurrentWeatherResponse(urlString);
        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResult> call, @NonNull Response<WeatherResult> response) {
                if (response.code() == 200) {
                    WeatherResult weatherResult = response.body();
                    Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                            .append(weatherResult.getWeather().get(0).getIcon())
                            .append(".png").toString()).into(imageView);
                    String wind = new StringBuffer("Wind: ").append(weatherResult.getWind().getSpeed()).append(" km/h").toString();
                    windTV.setText(wind);
                    String temp = new StringBuffer("Temp: ").append(weatherResult.getMain().getTemp()).append(" °C").toString();
                    tempTV.setText(temp);
                    String humidity = new StringBuffer("Humidity: ").append(weatherResult.getMain().getHumidity()).append(" %").toString();
                    humidityTV.setText(humidity);
                }
            }
            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    public void getForecastInformation() {
        //progressDialog.show();
        compositeDisposable.add(weatherService.getForeCastWeather(lat, lon, RetrofitClient.APP_ID, units)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        WeatherForcastAdapter adapter = new WeatherForcastAdapter(getContext(), weatherForecastResult);
                        weatherRV.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressDialog.dismiss();
                        Log.e(TAG, "Error: "+throwable.getMessage() );
                    }
                })

        );
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
