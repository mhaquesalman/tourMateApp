package com.salman.tourmateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.tourmateapp.fragment.ExpensesFragment;
import com.salman.tourmateapp.fragment.MemoriesFragment;
import com.salman.tourmateapp.fragment.TripsFragment;
import com.salman.tourmateapp.model.Trip;
import com.salman.tourmateapp.util.DatePickerFragment;
import com.salman.tourmateapp.util.DatePickerFragmentTwo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    EditText tripName;
    EditText tripDescription;
    static EditText tripStartDate;
    static EditText tripEndDate;
    TextInputLayout startDateTime, endDateTime;
    Button saveBtn, cancelBtn;
    FloatingActionButton fab;
    BottomNavigationView bottomNavigation;
    Fragment fragment = null;
    DatabaseReference databaseReference;
    static String startDate;
    static String endDate;
    DatePickerDialogFragment mDatePickerDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing views
        init();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TripsFragment()).commit();

        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        editor.apply();
        mDatePickerDialogFragment = new DatePickerDialogFragment();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: create dialog
                openDialog();
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_trips:
                        fragment = new TripsFragment();
                        break;
                    case R.id.nav_memories:
                        fragment = new MemoriesFragment();
                        break;
                    case R.id.nav_wallet:
                        fragment = new ExpensesFragment();
                        break;
                }
                if (fragment != null ){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                return true;
            }
        });
    }


    public void init() {
        fab = findViewById(R.id.fab);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    public void openDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.trips_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        tripName = dialog.findViewById(R.id.trip_name);
        tripDescription = dialog.findViewById(R.id.trip_desc);
        tripStartDate = dialog.findViewById(R.id.trip_start_date);
        tripEndDate = dialog.findViewById(R.id.trip_end_date);
        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);

        tripStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment datePicker = new DatePickerFragment();
                //datePicker.show(getSupportFragmentManager(), "date picker");

                mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_START_DATE);
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        tripEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment datePicker2 = new DatePickerFragmentTwo();
               // datePicker2.show(getSupportFragmentManager(), "date picker");

                mDatePickerDialogFragment.setFlag(DatePickerDialogFragment.FLAG_END_DATE);
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
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
                String str_tripName = tripName.getText().toString();
                String str_tripDesc = tripDescription.getText().toString();
                if (!TextUtils.isEmpty(str_tripName) || !TextUtils.isEmpty(str_tripDesc)
                        || !TextUtils.isEmpty(startDate) || !TextUtils.isEmpty(endDate)) {
                    saveTripInfo(str_tripName, str_tripDesc, startDate, endDate);
                } else {
                    Toast.makeText(MainActivity.this, "Empty field found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void saveTripInfo(String str_tripName, String str_tripDesc, String str_tripStartDate, String str_tripEndDate) {
        databaseReference = FirebaseDatabase.getInstance().getReference("trips");
        String tripid = databaseReference.push().getKey();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Trip trip = new Trip(tripid, str_tripName, str_tripDesc, str_tripStartDate, str_tripEndDate, userid);
        databaseReference.child(tripid).setValue(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Records added successfully !", Toast.LENGTH_SHORT).show();
                    tripName.setText("");
                    tripDescription.setText("");
                    tripStartDate.setText("");
                    tripEndDate.setText("");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

/*    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Calendar c1 = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        //String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        String DateFormat1 = new SimpleDateFormat("MMM d, yyyy").format(c.getTime());
        String DateFormat2 = new SimpleDateFormat("MMM d, yyyy").format(c1.getTime());
        startDateFormat = DateFormat1;
        endDateFormat = DateFormat2;
        tripStartDate.setText(startDateFormat);
        tripEndDate.setText(endDateFormat);

    }*/


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
                tripStartDate.setText(startDate);
            } else if (flag == FLAG_END_DATE) {
                endDate = Dateformat.format(c.getTime());
                tripEndDate.setText(endDate);


            }
        }
    }

}
