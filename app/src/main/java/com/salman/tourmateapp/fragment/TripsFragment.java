package com.salman.tourmateapp.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.activity.MainActivity;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.TripListAdapter;
import com.salman.tourmateapp.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment implements TripListAdapter.OnTripItemClickListener {
    private static final String TAG = "TripsFragment";
    AppCompatTextView tripName, tripDesc, tripStartDate, tripEndDate;
    RecyclerView recyclerView;
    List<Trip> tripList;
    DatabaseReference databaseReference;
    TripListAdapter tripAdapter;
    String userid;
    ProgressDialog progressDialog;
    EditText search_bar;
    EditText trip_Name;
    EditText trip_Description;
    static EditText trip_StartDate;
    static EditText trip_EndDate;
    Button saveBtn, cancelBtn;
    static String startDate;
    static String endDate;
    DatePickerDialogFragment mDatePickerDialogFragment;

    public TripsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        search_bar = view.findViewById(R.id.search_bar);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userid = preferences.getString("userid", "");

        mDatePickerDialogFragment = new DatePickerDialogFragment();
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        tripList = new ArrayList<>();
        tripAdapter = new TripListAdapter(tripList, getContext(), this);
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

    @Override
    public void onViewItem(String id) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.trip_details_dialog,null);
        dialog.setContentView(view);
        dialog.setCancelable(true);

        tripName = dialog.findViewById(R.id.tripNameTV);
        tripDesc = dialog.findViewById(R.id.tripDescTV);
        tripStartDate = dialog.findViewById(R.id.tripStartDateTV);
        tripEndDate = dialog.findViewById(R.id.tripEndDateTV);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trips").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                tripName.setText(trip.getTripName());
                tripDesc.setText(trip.getTripDesc());
                tripStartDate.setText(trip.getTripStartDate());
                tripEndDate.setText(trip.getTripEndDate());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog.show();
    }

    @Override
    public void onEditItem(String id) {
        getTripData(id);
    }

    public void getTripData(final String id) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.trips_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        trip_Name = dialog.findViewById(R.id.trip_name);
        trip_Description = dialog.findViewById(R.id.trip_desc);
        trip_StartDate = dialog.findViewById(R.id.trip_start_date);
        trip_EndDate = dialog.findViewById(R.id.trip_end_date);
        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);

        trip_StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment datePicker = new DatePickerFragment();
                //datePicker.show(getSupportFragmentManager(), "date picker");

                mDatePickerDialogFragment.setFlag(MainActivity.DatePickerDialogFragment.FLAG_START_DATE);
                mDatePickerDialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        trip_EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment datePicker2 = new DatePickerFragmentTwo();
                // datePicker2.show(getSupportFragmentManager(), "date picker");

                mDatePickerDialogFragment.setFlag(MainActivity.DatePickerDialogFragment.FLAG_END_DATE);
                mDatePickerDialogFragment.show(getFragmentManager(), "datePicker");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_tripName = trip_Name.getText().toString();
                String str_tripDesc = trip_Description.getText().toString();
                updateTripInfo(id, str_tripName, str_tripDesc, startDate, endDate);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trips").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                trip_Name.setText(trip.getTripName());
                trip_Description.setText(trip.getTripDesc());
                trip_StartDate.setText(trip.getTripStartDate());
                trip_EndDate.setText(trip.getTripEndDate());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void updateTripInfo(String id, String str_tripName, String str_tripDesc, String startDate, String endDate) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        final Trip trip = new Trip(id, str_tripName, str_tripDesc, startDate, endDate, userid);
        databaseReference.child(id).setValue(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Records updated successfully !", Toast.LENGTH_SHORT).show();
                            trip_Name.setText("");
                            trip_Description.setText("");
                            trip_StartDate.setText("");
                            trip_EndDate.setText("");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static class DatePickerDialogFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        public static final int FLAG_START_DATE = 0;
        public static final int FLAG_END_DATE = 1;
        private int flag = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setFlag(int i) {
            flag = i;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat Dateformat = new SimpleDateFormat("MMM d, yyyy");
            if (flag == FLAG_START_DATE) {
                startDate = Dateformat.format(c.getTime());
                trip_StartDate.setText(startDate);
            } else if (flag == FLAG_END_DATE) {
                endDate = Dateformat.format(c.getTime());
                trip_EndDate.setText(endDate);

            }
        }
    }
}
