package com.salman.tourmateapp.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.salman.tourmateapp.R;

public class TripDialog extends AppCompatDialogFragment {
    EditText tripName, tripDescription, tripStartDate, tripEndDate;
    Button saveBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.trips_dialog_layout, null);

        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        tripName = view.findViewById(R.id.trip_name);
        tripDescription = view.findViewById(R.id.trip_desc);
        tripStartDate = view.findViewById(R.id.trip_start_date);
        tripEndDate = view.findViewById(R.id.trip_end_date);

        return builder.create();
    }
}
