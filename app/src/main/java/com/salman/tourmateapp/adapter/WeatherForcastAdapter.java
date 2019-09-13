package com.salman.tourmateapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.tourmateapp.R;
import com.salman.tourmateapp.model.weather.WeatherForecastResult;
import com.salman.tourmateapp.util.Common;
import com.squareup.picasso.Picasso;

public class WeatherForcastAdapter extends RecyclerView.Adapter<WeatherForcastAdapter.MyViewHolder> {
    Context mContext;
    WeatherForecastResult forecastResult;

    public WeatherForcastAdapter(Context mContext, WeatherForecastResult forecastResult) {
        this.mContext = mContext;
        this.forecastResult = forecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String rowImage = new StringBuilder("https://openweathermap.org/img/w/")
                .append(forecastResult.list.get(i).weather.get(0).getIcon())
                .append(".png").toString();
        Picasso.get().load(rowImage).into(myViewHolder.rowImageView);

        String rowDate = Common.convertUnixToDate(forecastResult.list.get(i).dt);
        myViewHolder.rowDateTV.setText(rowDate);
        myViewHolder.mainTV.setText("" + forecastResult.list.get(i).weather.get(0).getDescription());
        myViewHolder.windTV.setText("W: " + forecastResult.list.get(i).wind.getSpeed() + " km/h");
        myViewHolder.tempTV.setText("T: " + forecastResult.list.get(i).main.getTemp() + " °C");
        myViewHolder.humidityTV.setText("H: " + forecastResult.list.get(i).main.getHumidity() + " %");


    }

    @Override
    public int getItemCount() {
        return forecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView rowImageView;
        TextView rowDateTV, mainTV, windTV, tempTV, humidityTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowImageView = itemView.findViewById(R.id.rowimageView);
            rowDateTV = itemView.findViewById(R.id.rowDateTV);
            mainTV = itemView.findViewById(R.id.mainTV);
            windTV = itemView.findViewById(R.id.windTV);
            tempTV = itemView.findViewById(R.id.tempTV);
            humidityTV = itemView.findViewById(R.id.humidityTV);
        }
    }
}
