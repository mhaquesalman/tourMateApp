package com.salman.tourmateapp.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.activity.SigninActivity;
import com.salman.tourmateapp.adapter.TripListAdapter;
import com.salman.tourmateapp.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment {
    private static final String TAG = "TripsFragment";
    RecyclerView recyclerView;
    List<Trip> tripList;
    DatabaseReference databaseReference;
    TripListAdapter tripAdapter;
    String userid;
    ProgressDialog progressDialog;
    EditText search_bar;
    public TripsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        search_bar = view.findViewById(R.id.search_bar);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userid = preferences.getString("userid", "");

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        tripList = new ArrayList<>();
        tripAdapter = new TripListAdapter(tripList, getContext());
        recyclerView.setAdapter(tripAdapter);

        myTrips();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTrip(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    public void searchTrip(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("trips").orderByChild("tripStartDate")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Trip trip = snapshot.getValue(Trip.class);
                    tripList.add(trip);
                }
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }


    private void myTrips() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip trip = snapshot.getValue(Trip.class);
                    if (trip.getUserId().equals(userid)) {
                        progressDialog.dismiss();
                        tripList.add(trip);
                        Log.d(TAG, "onDataChange: "+ tripList);
                    }
                }
                tripAdapter.notifyDataSetChanged();
                Collections.reverse(tripList);
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
