package com.salman.tourmateapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.salman.tourmateapp.R;
import com.salman.tourmateapp.activity.SendActivity;
import com.salman.tourmateapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private List<User> userList;
    private Context mContext;

    public UserListAdapter(List<User> userList, Context mContext) {
        this.userList = userList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
       User user = userList.get(i);
       holder.nameTV.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageUrl()).apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(holder.image);

        final String user_id = userList.get(i).getUserid();
        final String user_name = userList.get(i).getFullname();

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
                Intent intent = new Intent(mContext, SendActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("user_name", user_name);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nameTV;
    CircleImageView image;
    LinearLayout linearLayout;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTV = itemView.findViewById(R.id.nameTV);
        image = itemView.findViewById(R.id.image);
        linearLayout = itemView.findViewById(R.id.linearLayout);
    }
}

}
