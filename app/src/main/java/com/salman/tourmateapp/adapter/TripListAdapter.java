package com.salman.tourmateapp.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.activity.SignupActivity;
import com.salman.tourmateapp.fragment.MemoriesFragment;
import com.salman.tourmateapp.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.MyViewHolder> {
    List<Trip> tripList;
    Context mContext;
    OnTripItemClickListener listener;

    public TripListAdapter(List<Trip> tripList, Context mContext, OnTripItemClickListener listener) {
        this.tripList = tripList;
        this.mContext = mContext;
        this.listener = listener;
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
                listener.onViewItem(trip.getTripId());
            }
        });

        myViewHolder.tripMemoriesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditItem(trip.getTripId());
            }
        });

        myViewHolder.tripDeleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(trip);
            }
        });
    }


    public void showConfirmationDialog(final Trip trip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Item");
        //builder.setIcon(R.drawable.ic_delete_red_24dp);
        builder.setMessage("Delete this item ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteTrip(trip);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteTrip(final Trip trip) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trips").child(trip.getTripId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Records deleted successfully !", Toast.LENGTH_SHORT).show();
                    tripList.remove(trip);
                    notifyDataSetChanged();
                }
            }
        });
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

    public interface OnTripItemClickListener {
        void onViewItem(String id);
        void onEditItem(String id);
    }

}
