package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.databinding.ActivitySigninBinding;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity {
    public static final String TAG = "SigninActivity";
    ActivitySigninBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "Firebase User: OnStart " + firebaseUser);
        if (firebaseUser != null) {
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin);

        init();
        Log.d(TAG, "Firebase User: OnCreate " + firebaseUser);
        binding.signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            }
        });

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(SigninActivity.this);
        progressDialog.setMessage("Please wait...");
    }

    public void signInUser(View view) {
        String str_email = binding.email.getText().toString();
        String str_password = binding.password.getText().toString();
        if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
            Toast.makeText(SigninActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {
            userLogin(str_email, str_password);
        }
    }

    public void userLogin(String str_email, String str_password) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(str_email, str_password)
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //String tokenID = FirebaseInstanceId.getInstance().getToken();
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    String token_id = task.getResult().getToken();
                                    String current_id = mAuth.getCurrentUser().getUid();
                                    Log.d(TAG, "onComplete: " + current_id + " | " + firebaseUser);
                                    Map<String, Object> tokenMap = new HashMap<>();
                                    tokenMap.put("tokenId", token_id);
                                    databaseReference.child("users").child(current_id).updateChildren(tokenMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SigninActivity.this, "login failed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SigninActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}




