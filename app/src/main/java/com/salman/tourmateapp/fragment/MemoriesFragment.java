package com.salman.tourmateapp.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.adapter.MemoryListAdapter;
import com.salman.tourmateapp.model.Memory;
import com.salman.tourmateapp.model.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment implements MemoryListAdapter.OnRowItemClickListener {
    private static final String TAG = "MemoriesFragment";
    TextView add_memory;
    Button addImageBtn, saveBtn, cancelBtn;
    EditText memoryDesc;
    ImageView memoryImage;
    Spinner spinner;
    private Uri uri;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    ValueEventListener listener;
    DatabaseReference reference;
    String tripLocation, userid;
    ProgressDialog progressDialog;
    RecyclerView memoryRecyclerView;
    List<Memory> memoryList;
    MemoryListAdapter memoryListAdapter;
    public MemoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_memories, container, false);


        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        userid = preferences.getString("userid", "");

        memoryRecyclerView = view.findViewById(R.id.memoriesRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        memoryRecyclerView.setLayoutManager(layoutManager);
        memoryRecyclerView.setHasFixedSize(true);

        memoryList = new ArrayList<>();
        memoryListAdapter = new MemoryListAdapter(memoryList, getContext(),this);
        memoryRecyclerView.setAdapter(memoryListAdapter);

        add_memory = view.findViewById(R.id.add_memory);
        add_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        getMemories();
        Log.d(TAG, "onCreateView: Uri-> " + uri);
        return view;
    }


    public void getMemories(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("memories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                memoryList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Memory memory = item.getValue(Memory.class);
                    if (memory.getUserId().equals(userid)) {
                        memoryList.add(memory);
                    }
                    memoryListAdapter.notifyDataSetChanged();
                    Collections.reverse(memoryList);
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getTripData() {
        reference = FirebaseDatabase.getInstance().getReference("trips");
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                     Trip trip = item.getValue(Trip.class);
                     String tripName = trip.getTripName();
                     arrayList.add(tripName);
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
        View view = getLayoutInflater().inflate(R.layout.memories_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        addImageBtn = dialog.findViewById(R.id.addImage_btn);
        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);
        memoryImage = dialog.findViewById(R.id.memory_image);
        memoryDesc = dialog.findViewById(R.id.memory_desc);
        spinner = dialog.findViewById(R.id.spinner);
        memoryImage = dialog.findViewById(R.id.memory_image);

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

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
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
                String str_memoryDesc = memoryDesc.getText().toString();
                saveMemory(str_memoryDesc);
            }
        });
        dialog.show();
    }

    private void saveMemory(final String str_memoryDesc) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        final StorageReference imageStorage = FirebaseStorage.getInstance().getReference()
                .child("trip-images").child(System.currentTimeMillis()+"."+getFileExtension(uri));
        if (uri != null) {
            imageStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("memories");
                                String memoryId = reference.push().getKey();
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Memory memory = new Memory(memoryId, imageUrl, str_memoryDesc, tripLocation, userId);
                                reference.child(memoryId).setValue(memory)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Records added successfully !", Toast.LENGTH_SHORT).show();
                                                    memoryDesc.setText("");
                                                    memoryImage.setImageResource(R.drawable.image_placeholder);
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
                        });
                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            Toast.makeText(getActivity(), "image added", Toast.LENGTH_SHORT).show();
            memoryImage.setImageURI(uri);
            Log.d(TAG, "onActivityResult: Uri-> " + uri);
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onEditItemClicked(String id) {
        Log.d("memoryFragment", "onEditItemClicked: ");
        getMemoryData(id);
    }

    public void getMemoryData(final String id){
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        View view = getLayoutInflater().inflate(R.layout.memories_dialog_layout,null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        addImageBtn = dialog.findViewById(R.id.addImage_btn);
        saveBtn = dialog.findViewById(R.id.save_btn);
        cancelBtn = dialog.findViewById(R.id.cancel_btn);
        memoryImage = dialog.findViewById(R.id.memory_image);
        memoryDesc = dialog.findViewById(R.id.memory_desc);
        spinner = dialog.findViewById(R.id.spinner);
        memoryImage = dialog.findViewById(R.id.memory_image);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.color_spinner_layout, arrayList);
        spinner.setAdapter(arrayAdapter);
        getTripData();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tripLocation = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.yellow));
                Toast.makeText(parent.getContext(), "Selected: " + tripLocation, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
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
                String str_memoryDesc = memoryDesc.getText().toString();
                updateMemory(str_memoryDesc, id);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("memories").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Memory memory = dataSnapshot.getValue(Memory.class);
                memoryDesc.setText(memory.getMemoryDesc());
                arrayList.add(0, memory.getTripLocatiion());
                spinner.setSelection(0);
                Glide.with(getActivity()).load(memory.getMemoryimage()).into(memoryImage);
                Log.d(TAG, "getMemory: Uri-> " + uri);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    public void updateMemory(final String str_memoryDesc, final String id) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        if (uri != null) {
            final StorageReference imageStorage = FirebaseStorage.getInstance().getReference()
                    .child("trip-images").child(System.currentTimeMillis()+"."+getFileExtension(uri));
            imageStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("memories");
                                //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                Memory memory = new Memory(id, imageUrl, str_memoryDesc, tripLocation, userid);
                                reference.child(id).setValue(memory)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Records updated successfully !", Toast.LENGTH_SHORT).show();
                                                    memoryDesc.setText("");
                                                    memoryImage.setImageResource(R.drawable.image_placeholder);
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
                        });
                    }
                }
            });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("memories").child(id);
            //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Map<String,Object> hashmap = new HashMap<>();
            hashmap.put("memoryDesc", str_memoryDesc);
            hashmap.put("tripLocation",tripLocation);
            reference.updateChildren(hashmap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Records updated successfully !", Toast.LENGTH_SHORT).show();
                                memoryDesc.setText("");
                                memoryImage.setImageResource(R.drawable.image_placeholder);
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
}
