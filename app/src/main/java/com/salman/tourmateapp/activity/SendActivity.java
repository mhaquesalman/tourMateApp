package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.tourmateapp.R;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {
    TextView user_id_view;
    EditText messageText;
    Button sendmsg_btn, checkmsg_btn;
    Toolbar toolbar;
    String mUserId, mUserName, mCurrentId;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        user_id_view = findViewById(R.id.user_id_view);
        messageText = findViewById(R.id.message);
        sendmsg_btn = findViewById(R.id.sendmsg_btn);
        checkmsg_btn = findViewById(R.id.checkmsg_btn);

        //toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(SendActivity.this);
        progressDialog.setMessage("Please wait...");

        if (getIntent() != null) {
            mUserId = getIntent().getStringExtra("user_id");
            mUserName = getIntent().getStringExtra("user_name");
            user_id_view.setText("Send to : " + mUserName);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentId = mAuth.getCurrentUser().getUid();
        //FirebaseAuth.getInstance().getUid()


        sendmsg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    progressDialog.show();
                    Map<String, Object> notificationMsg = new HashMap<>();
                    notificationMsg.put("message", message);
                    notificationMsg.put("from", mCurrentId);
                    DatabaseReference userRef = databaseReference.child("users").child(mUserId).child("notifications");
                    String notificationId = databaseReference.push().getKey();
                    userRef.child(notificationId).setValue(notificationMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(SendActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                            messageText.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SendActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SendActivity.this, "Empty Message", Toast.LENGTH_SHORT).show();
                    messageText.requestFocus();
                }
            }
        });

        checkmsg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.salman.tourmateapp.activity.NotificationListActivity");
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

   @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
