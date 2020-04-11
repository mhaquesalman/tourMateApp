package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.NotificationAdapter;
import com.salman.tourmateapp.adapter.UserListAdapter;
import com.salman.tourmateapp.model.Notification;
import com.salman.tourmateapp.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    List<Notification> notificationList;
    TextView mNotificationData;
    RecyclerView notificationView;
    ProgressDialog progressDialog;
    NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mNotificationData = findViewById(R.id.notify_text);
        notificationView = findViewById(R.id.notficationView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        notificationView.setLayoutManager(layoutManager);
        notificationView.setHasFixedSize(true);

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, this);
        notificationView.setAdapter(notificationAdapter);

        if (getIntent() != null) {
            String dataMessage = getIntent().getStringExtra("message");
            String dataFrom = getIntent().getStringExtra("from_user_id");
            String senderName = getIntent().getStringExtra("from_name");

            mNotificationData.setText(senderName + " | " + "Message : " + dataMessage);
        } else {
            mNotificationData.setText("Nothing Found !");
        }

        getNotifications();
    }

    public void getNotifications() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SharedPreferences myPrefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        String userId = myPrefs.getString("userid", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Notification notification = item.getValue(Notification.class);
                    notificationList.add(notification);
                }
                progressDialog.dismiss();
                Collections.reverse(notificationList);
                notificationAdapter.notifyDataSetChanged();

                if (notificationList.isEmpty()) {
                    Toast.makeText(NotificationActivity.this, "no data found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(NotificationActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
