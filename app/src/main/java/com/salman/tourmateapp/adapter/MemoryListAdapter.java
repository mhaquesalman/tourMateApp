package com.salman.tourmateapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.model.Memory;

import java.util.List;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    List<Memory> memoryList;
    Context mContext;
    OnRowItemClickListener listener;
    public MemoryListAdapter(List<Memory> memoryList, Context mContext, OnRowItemClickListener listener) {
        this.memoryList = memoryList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.memory_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Memory memory = memoryList.get(i);
        Glide.with(mContext).load(memory.getMemoryimage()).apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(myViewHolder.memoryImage);

        myViewHolder.placeName.setText(memory.getTripLocatiion());
        myViewHolder.memoryDesc.setText(memory.getMemoryDesc());

        myViewHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.memory_row_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.item_edit:
                                listener.onEditItemClicked(memory.getMemoryId());
                                Log.d("onAdapter", "onMenuItemClick: "+listener.toString() + " " + memory.getMemoryId());
                                //editMemory(memory.getMemoryId());
                                break;
                            case R.id.item_delete:
                                showConfirmationDialog(memory);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void showConfirmationDialog(final Memory memory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Item");
        //builder.setIcon(R.drawable.ic_delete_red_24dp);
        builder.setMessage("Delete this item ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMemory(memory);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteMemory(final Memory memory){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("memories").child(memory.getMemoryId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Records deleted successfully !", Toast.LENGTH_SHORT).show();
                    memoryList.remove(memory);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView memoryImage,itemMenu;
        TextView placeName, memoryDesc;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            memoryImage = itemView.findViewById(R.id.memoryImage);
            placeName = itemView.findViewById(R.id.placeName);
            memoryDesc = itemView.findViewById(R.id.memoryDesc);
            itemMenu = itemView.findViewById(R.id.item_menu);
        }
    }

    public interface OnRowItemClickListener {
        void onEditItemClicked(String id);
    }
}
