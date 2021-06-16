package com.example.expensetracker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class ViewHolder2 extends RecyclerView.ViewHolder {

    public TextView mDate, mType, mNote, mAmount;

    public ViewHolder2(@NonNull @NotNull View itemView) {
        super(itemView);

        mDate = itemView.findViewById(R.id.date_txt_expense);
        mType = itemView.findViewById(R.id.type_txt_expense);
        mNote = itemView.findViewById(R.id.note_txt_expense);
        mAmount = itemView.findViewById(R.id.amount_txt_expense);
    }

    public void setDate(String Date) {
        mDate.setText(Date);
    }

    public void setType(String Type) {
        mType.setText(Type);
    }

    public void setNote(String Note) {
        mNote.setText(Note);
    }

    public void setAmount(String Amount) {
        mAmount.setText(Amount);
    }
}
