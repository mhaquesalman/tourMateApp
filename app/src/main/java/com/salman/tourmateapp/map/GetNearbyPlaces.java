package com.salman.tourmateapp.map;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    GoogleMap mMap;
    String url;
    InputStream inputStream;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;
    Context mContext;

    public GetNearbyPlaces(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        url = (String) params[1];

        try {
            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        //Toast.makeText(mContext, ""+s, Toast.LENGTH_SHORT).show();
        try {
            JSONObject parentObject = new JSONObject(s);
            JSONArray resultArray = parentObject.getJSONArray("results");
            for (int i = 0; i<resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject locationObj = jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude = locationObj.getString("lat");
                String longitude = locationObj.getString("lng");

                JSONObject nameObj = resultArray.getJSONObject(i);
                String placeName = nameObj.getString("name");
                String vicinity = nameObj.getString("vicinity");

                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                //mMap.animateCamera(update);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(placeName);
                markerOptions.position(latLng);
                //markerOptions.snippet()
                mMap.addMarker(markerOptions);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
