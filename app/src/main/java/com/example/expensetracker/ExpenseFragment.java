package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    //Recycler view inside Fragment
    private RecyclerView recyclerView;


    //Firebase
    private FirebaseRecyclerAdapter adapter;
    private Query query;
    private DatabaseReference mExpenseDatabase;
    private String uid;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    //View in main fragmnet
    private View view;

    //Textview of type,amount,note
    private EditText mNoteEt;
    private EditText mTypeEt;
    private EditText mAmountEt;


    //post key as id for update and delete
    private String post_key;

    //String for updating data
    private String mNote;
    private String mType;
    private int mAmount;

    //Total expense Txtview
    private TextView mTotalExpense;

    //update and delete button
    private android.widget.Button mUpdatebtn;
    private android.widget.Button mDeletebtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mTotalExpense = view.findViewById(R.id.total_expense_tv);
        fetchData();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int totalExpense = 0;
                for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                    Data data = mSnapshot.getValue(Data.class);
                    totalExpense += data.getAmount();
                    mTotalExpense.setText(String.valueOf(totalExpense));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }

    private void fetchData() {
        recyclerView = view.findViewById(R.id.recycler_expense);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        //Getting current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        query = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
                    @NonNull
                    @NotNull
                    @Override
                    public Data parseSnapshot(@NonNull @NotNull DataSnapshot snapshot) {
                        return new Data(Integer.parseInt(snapshot.child("amount").getValue().toString()),
                                snapshot.child("type").getValue().toString(),
                                snapshot.child("note").getValue().toString(),
                                snapshot.child("id").getValue().toString(),
                                snapshot.child("date").getValue().toString());
                    }
                }).build();
        adapter = new FirebaseRecyclerAdapter<Data, ViewHolder2>(options) {

            @NonNull
            @NotNull
            @Override
            public ViewHolder2 onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycler_data, parent, false);
                return new ViewHolder2(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull ViewHolder2 holder, int position, @NonNull @NotNull Data model) {
                holder.setType(model.getType());
                holder.setAmount(String.valueOf(model.getAmount()));
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        mType = model.getType();
                        mNote = model.getNote();
                        mAmount = model.getAmount();
                        updateDataItem();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void updateDataItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View alertDialogView = inflater.inflate(R.layout.update_data_item, null);
        myDialog.setView(alertDialogView);

        mTypeEt = alertDialogView.findViewById(R.id.type_et);
        mAmountEt = alertDialogView.findViewById(R.id.amount_et);
        mNoteEt = alertDialogView.findViewById(R.id.note_et);


        //Setting data in edit text from database

        mTypeEt.setText(mType);
        mTypeEt.setSelection(mType.length());

        mNoteEt.setText(mNote);
        mNoteEt.setSelection(mNote.length());

        mAmountEt.setText(String.valueOf(mAmount));
        mAmountEt.setSelection(String.valueOf(mAmount).length());


        AlertDialog dialog = myDialog.create();

        mUpdatebtn = alertDialogView.findViewById(R.id.update_btn);
        mDeletebtn = alertDialogView.findViewById(R.id.delete_btn);


        mUpdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote = mNoteEt.getText().toString().trim();
                mType = mTypeEt.getText().toString().trim();
                mAmount = Integer.parseInt(mAmountEt.getText().toString().trim());

                if (TextUtils.isEmpty(mNote)) {
                    mNoteEt.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(mType)) {
                    mTypeEt.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(String.valueOf(mAmount))) {
                    mAmountEt.setError("Required field!!");
                    return;
                }
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mAmount, mType, mNote, post_key, mDate);
                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        mDeletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpenseDatabase.child(post_key).removeValue();
                mDeletebtn.setEnabled(false);
                dialog.dismiss();
                mDeletebtn.setEnabled(true);
            }
        });
        dialog.show();
    }

}