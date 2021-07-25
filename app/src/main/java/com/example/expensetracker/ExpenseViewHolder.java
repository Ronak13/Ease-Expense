package com.example.expensetracker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class ExpenseViewHolder extends RecyclerView.ViewHolder {

    public TextView mDate, mType, mAmount;

    public ExpenseViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        mDate = itemView.findViewById(R.id.date_expense_ds);
        mType = itemView.findViewById(R.id.type_expense_ds);
        mAmount = itemView.findViewById(R.id.amount_expense_ds);
    }

    public void setDate(String Date) {
        mDate.setText(Date);
    }

    public void setType(String Type) {
        mType.setText(Type);
    }

    public void setAmount(String Amount) {
        mAmount.setText(Amount);
    }
}
