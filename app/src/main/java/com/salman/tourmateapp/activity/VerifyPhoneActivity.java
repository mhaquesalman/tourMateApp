package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.salman.tourmateapp.R;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {

    EditText codeET;
    Button signinBtn;
    TextView sendCodeTV, reSendCodeTV;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String mobile;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        progressBar = findViewById(R.id.progressbar);
        codeET = findViewById(R.id.codeET);
        signinBtn = findViewById(R.id.signinBtn);
        sendCodeTV = findViewById(R.id.sendCodeTV);
        reSendCodeTV = findViewById(R.id.resendCodeTV);
        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        if (intent != null) {
            mobile = intent.getStringExtra("mobile");
            Log.d("Phone", "Phone: "+mobile);
        }

        sendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(mobile);
            }
        });

        reSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mobile, mResendToken);
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeET.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    codeET.setError("Enter valid code");
                    codeET.requestFocus();
                    return;
                }
                //verifying the code entered manually
                verifyCode(code);
            }
        });

    }

    private void verifyCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        //signInWithPhoneAuthCredential(credential);
        Toast.makeText(getApplicationContext(), "Verificatiion Successfull..", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }


    private void resendVerificationCode(String mobile,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+88" + mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        Toast.makeText(VerifyPhoneActivity.this,"SMS Resent, Please Wait....", Toast.LENGTH_LONG).show();
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                codeET.setText(code);
                //verifying the code
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            //storing the verification id that is sent to the user
            mVerificationId = s;
            mResendToken = token;
            Toast.makeText(VerifyPhoneActivity.this,"SMS Sent...", Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verificatiion Successfull..", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(VerifyPhoneActivity.this, SigninActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}
