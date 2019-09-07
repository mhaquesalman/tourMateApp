package com.salman.tourmateapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.model.Memory;

import java.util.List;

public class MemoryListAdapter extends RecyclerView.Adapter<MemoryListAdapter.MyViewHolder> {
    List<Memory> memoryList;
    Context mContext;

    public MemoryListAdapter(List<Memory> memoryList, Context mContext) {
        this.memoryList = memoryList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.memory_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Memory memory = memoryList.get(i);
        Glide.with(mContext).load(memory.getMemoryimage()).apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(myViewHolder.memoryImage);

        myViewHolder.placeName.setText(memory.getTripLocatiion());
        myViewHolder.memoryDesc.setText(memory.getMemoryDesc());
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView memoryImage;
        TextView placeName, memoryDesc;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             memoryImage = itemView.findViewById(R.id.memoryImage);
             placeName = itemView.findViewById(R.id.placeName);
             memoryDesc = itemView.findViewById(R.id.memoryDesc);
        }
    }
}
