package com.salman.tourmateapp.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.ExpenseListAdapter;
import com.salman.tourmateapp.adapter.MemoryListAdapter;
import com.salman.tourmateapp.model.Expense;
import com.salman.tourmateapp.model.Memory;
import com.salman.tourmateapp.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpensesFragment extends Fragment implements ExpenseListAdapter.OnExpenseItemClickListner {
    TextView add_expense, defaultTV;
    RecyclerView expenseRV;
    EditText expense_desc, expense_balance;
    Button saveBtn, cancelBtn;
    Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    String tripLocation;
    String userid;
    ProgressDialog progressDialog;
    List<Expense> expenseList = new ArrayList<>();
    ExpenseListAdapter expenseListAdapter;

    public ExpensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userid = preferences.getString("userid", "");

        expenseRV = view.findViewById(R.id.expenseRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        expenseRV.setLayoutManager(layoutManager);
        expenseRV.setHasFixedSize(true);

        expenseListAdapter = new ExpenseListAdapter(expenseList, getActivity(),this);
        expenseRV.setAdapter(expenseListAdapter);

        defaultTV = view.findViewById(R.id.defaultTV);
        add_expense = view.findViewById(R.id.add_expense);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        getExpense();
        return view;
    }

    public void getTripData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trips");
        ValueEventListener listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Trip trip = item.getValue(Trip.class);
                    if (trip.getUserId().equals(userid)) {
                        String tripName = trip.getTripName();
                        arrayList.add(tripName);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.expense_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);
        expense_desc = dialog.findViewById(R.id.expense_desc);
        expense_balance = dialog.findViewById(R.id.expense_balance);
        spinner = dialog.findViewById(R.id.spinner);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.color_spinner_layout, arrayList);
        spinner.setAdapter(arrayAdapter);
        getTripData();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tripLocation = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + tripLocation, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                String str_expenseDesc = expense_desc.getText().toString();
                String str_expenseBalance = expense_balance.getText().toString();
                saveExpense(str_expenseDesc, str_expenseBalance);
            }
        });
        dialog.show();
    }

    public void saveExpense(String str_expenseDesc, String str_expenseBalance) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses");
        String expenseId = reference.push().getKey();
        Expense expense = new Expense(expenseId, str_expenseDesc, str_expenseBalance, tripLocation, userid);
        reference.child(expenseId).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Records added successfully !", Toast.LENGTH_SHORT).show();
                    expense_desc.setText("");
                    expense_balance.setText("");
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

    public void getExpense() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenseList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Expense expense = item.getValue(Expense.class);
                    if (expense.getUserId().equals(userid)) {
                        expenseList.add(expense);
                    }
                }
                progressDialog.dismiss();
                Collections.reverse(expenseList);
                expenseListAdapter.notifyDataSetChanged();
                if (expenseList.isEmpty()) {
                    defaultTV.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickItemEdit(String id) {
        getExpenseData(id);
    }

    public void getExpenseData(final String id) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.expense_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);
        expense_desc = dialog.findViewById(R.id.expense_desc);
        expense_balance = dialog.findViewById(R.id.expense_balance);
        spinner = dialog.findViewById(R.id.spinner);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.color_spinner_layout, arrayList);
        spinner.setAdapter(arrayAdapter);
        getTripData();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tripLocation = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + tripLocation, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                String str_expenseDesc = expense_desc.getText().toString();
                String str_expenseBalance = expense_balance.getText().toString();
                updateExpense(id,str_expenseDesc, str_expenseBalance);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Expense expense = dataSnapshot.getValue(Expense.class);
                expense_desc.setText(expense.getExpenseDesc());
                expense_balance.setText(expense.getExpenseBal());
                arrayList.add(0, expense.getTripLocation());
                spinner.setSelection(0);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void updateExpense(String id, String str_expenseDesc, String str_expenseBalance) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses");
        Expense expense = new Expense(id, str_expenseDesc, str_expenseBalance, tripLocation, userid);
        reference.child(id).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Records updated successfully !", Toast.LENGTH_SHORT).show();
                    expense_desc.setText("");
                    expense_balance.setText("");
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

}
