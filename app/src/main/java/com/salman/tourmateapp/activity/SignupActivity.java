package com.salman.tourmateapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.salman.tourmateapp.MainActivity;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.model.User;

public class SignupActivity extends AppCompatActivity {
    ImageView imageView;
    EditText fullname, email, phone, password;
    TextView signinTV;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private static final int IMAGE_REQUEST_CODE = 1;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

    }

    public void init() {
        imageView = findViewById(R.id.imageView);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        signinTV = findViewById(R.id.signin_TV);

        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("Please wait...");

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUpUser(View view) {
        String str_fullname = fullname.getText().toString();
        String str_email = email.getText().toString();
        String str_phone = phone.getText().toString();
        String str_password = password.getText().toString();

        if (TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_phone) || TextUtils.isEmpty(str_password)) {
            Toast.makeText(SignupActivity.this, "Empty field found", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (str_password.length() < 6){
            password.setError("at least 6 characters required");
        } else {
            userRegister(str_fullname, str_email, str_phone, str_password);
        }

    }

    public void userRegister(final String str_fullname, final String str_email, final String str_phone, final String str_password) {
        progressDialog.show();
        final StorageReference imageStorage = storageReference.child("profile-images").child(System.currentTimeMillis()+"."+getFileExtension(uri));
        if (uri != null) {
            imageStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String imageUrl = uri.toString();
                                mAuth.createUserWithEmailAndPassword(str_email, str_password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        User user = new User(userId,str_fullname, str_email, str_phone, imageUrl);

                                        DatabaseReference userRef = databaseReference.child("users").child(userId);
                                        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignupActivity.this, "Sign up complete", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignupActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    public void addImage(View view) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        opengallery();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(SignupActivity.this, "Permission is required", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void opengallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
           uri = data.getData();
           Toast.makeText(this, "image added", Toast.LENGTH_SHORT).show();

       }
    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
