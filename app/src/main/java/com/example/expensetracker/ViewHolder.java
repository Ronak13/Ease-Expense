package com.example.expensetracker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView mDate, mType, mNote, mAmount;

    public ViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        mDate = itemView.findViewById(R.id.date_txt_income);
        mType = itemView.findViewById(R.id.type_txt_income);
        mNote = itemView.findViewById(R.id.note_txt_income);
        mAmount = itemView.findViewById(R.id.amount_txt_income);
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
