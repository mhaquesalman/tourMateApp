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
import com.salman.tourmateapp.model.Expense;
import com.salman.tourmateapp.model.Memory;

import java.util.List;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.MyViewHolder> {
    List<Expense> expenseList;
    Context mContext;
    OnExpenseItemClickListner listner;
    public ExpenseListAdapter(List<Expense> expenseList, Context mContext, OnExpenseItemClickListner listner) {
        this.expenseList = expenseList;
        this.mContext = mContext;
        this.listner = listner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.expense_recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Expense expense = expenseList.get(i);

        myViewHolder.placeName.setText(expense.getTripLocation());
        myViewHolder.expenseDesc.setText(expense.getExpenseDesc());
        myViewHolder.expenseBalance.setText(expense.getExpenseBal());

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
                                listner.onClickItemEdit(expense.getExpenseId());
                                break;
                            case R.id.item_delete:
                                showConfirmationDialog(expense);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView placeName, expenseDesc, expenseBalance;
        ImageView itemMenu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            expenseDesc = itemView.findViewById(R.id.expenseDesc);
            expenseBalance = itemView.findViewById(R.id.expenseBalance);
            itemMenu = itemView.findViewById(R.id.item_menu);
        }
    }


    private void showConfirmationDialog(final Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Item");
        //builder.setIcon(R.drawable.ic_delete_red_24dp);
        builder.setMessage("Delete this item ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteExpense(expense);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteExpense(final Expense expense){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(expense.getExpenseId());
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Records deleted successfully !", Toast.LENGTH_SHORT).show();
                    expenseList.remove(expense);
                    notifyDataSetChanged();
                }
            }
        });

    }

    public interface OnExpenseItemClickListner {
        void onClickItemEdit(String id);
    }
}
