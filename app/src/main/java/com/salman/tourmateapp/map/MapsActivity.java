package com.salman.tourmateapp.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.map.GetNearbyPlaces;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapsActivity";
    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrent;
    String keyword;
    Handler mhandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent() != null) {
            String index = getIntent().getStringExtra("index");
            Log.d(TAG, "Index: "+index);
            //keyword = index;
           switch (index) {
                case "0":
                    keyword = "hospital";
                    break;
                case "1":
                    keyword = "resturant";
                    break;
                case "2":
                    keyword = "school";
                    break;
                case "3":
                    keyword = "atm";
            }
        }

        mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //doStuff();
                findPlaces();
            }
        }, 10000);
    }

    private void doStuff() {
        //Toast.makeText(this, ""+latLngCurrent.latitude + "/" + latLngCurrent.longitude, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, ""+keyword, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "findPlaces: "+latLngCurrent.latitude + "/" + latLngCurrent.longitude);
    }

    public void findPlaces() {
        Toast.makeText(this, "initialize...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "findPlaces: "+latLngCurrent.latitude + "/" + latLngCurrent.longitude);
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," +latLngCurrent.longitude);
        stringBuilder.append("&radius="+1000);
        stringBuilder.append("&keyword="+keyword);
        stringBuilder.append("&key="+getResources().getString(R.string.api_key));

        String url = stringBuilder.toString();
        Object[] dataTransfer = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces(this);
        getNearbyPlaces.execute(dataTransfer);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "location not found !", Toast.LENGTH_SHORT).show();
        } else {
            latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d(TAG, "onLocation: "+latLngCurrent.latitude + "/" + latLngCurrent.longitude);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15);
            mMap.animateCamera(update);
            MarkerOptions options = new MarkerOptions();
            options.position(latLngCurrent);
            options.title("Current Location");
            mMap.addMarker(options);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
