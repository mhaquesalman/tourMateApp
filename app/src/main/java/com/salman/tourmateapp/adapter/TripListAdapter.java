package com.salman.tourmateapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salman.tourmateapp.R;
import com.salman.tourmateapp.model.Trip;

import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.MyViewHolder> {
    List<Trip> tripList;
    Context mContext;

    public TripListAdapter(List<Trip> tripList, Context mContext) {
        this.tripList = tripList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trip_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Trip trip = tripList.get(i);
        myViewHolder.tripNameTV.setText(trip.getTripName());
        myViewHolder.tripDescTV.setText(trip.getTripDesc());

        myViewHolder.tripDetailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        myViewHolder.tripMemoriesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        myViewHolder.tripDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void openDialog() {
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tripNameTV, tripDescTV, tripDetailsTV, tripMemoriesTV, tripDeleteTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tripNameTV = itemView.findViewById(R.id.tripNameTV);
            tripDescTV = itemView.findViewById(R.id.tripDescTV);
            tripDetailsTV = itemView.findViewById(R.id.tripDetailsTV);
            tripMemoriesTV = itemView.findViewById(R.id.tripMemoriesTV);
            tripDeleteTV = itemView.findViewById(R.id.tripDeleteTV);
        }
    }
}
