package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.salman.tourmateapp.MainActivity;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {
    ActivitySigninBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signin);

        init();

        binding.signupTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this,SignupActivity.class));
            }
        });

    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
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
                            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SigninActivity.this, "login failed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SigninActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
