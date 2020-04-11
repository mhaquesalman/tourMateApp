package com.salman.tourmateapp.fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.UserListAdapter;
import com.salman.tourmateapp.model.Memory;
import com.salman.tourmateapp.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    public static final String TAG = "UserFragment";
    List<User> userList;
    UserListAdapter userListAdapter;
    RecyclerView usersRV;
    ProgressDialog progressDialog;
    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        usersRV = view.findViewById(R.id.usersRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        usersRV.setLayoutManager(layoutManager);
        usersRV.setHasFixedSize(true);

        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList, getContext());
        usersRV.setAdapter(userListAdapter);

        //get all users
        getUsers();
        return view;
    }

    public void getUsers() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SharedPreferences myPrefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        final String userId = myPrefs.getString("userid", "");
        Log.d(TAG, "getUsers: id: " + userId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    User user = item.getValue(User.class);
                    if (!user.getUserid().equals(userId)) {
                        userList.add(user);
                    }
                }
                progressDialog.dismiss();
                //Collections.reverse(memoryList);
                userListAdapter.notifyDataSetChanged();
                if (userList.isEmpty()) {
                    Toast.makeText(getContext(), "no data found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
