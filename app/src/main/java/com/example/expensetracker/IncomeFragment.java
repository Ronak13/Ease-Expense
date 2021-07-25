package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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


public class IncomeFragment extends Fragment {


    //Recyclerview inside fragment
    private RecyclerView recyclerView;

    //Firebase
    private FirebaseRecyclerAdapter adapter;
    private Query query;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private DatabaseReference mIncomeDatabase;


    //View main of fragment
    private View view;

    // Textview income
    private TextView mIncomeTotal;

    //Updste edit text;
    private EditText mAmountEt;
    private EditText mNoteEt;
    private EditText mTypeEt;

    // Update and delete Button
    private android.widget.Button mUpdatebtn;
    private android.widget.Button mDeletebtn;

    // String for data update
    private String mType;
    private int mAmount;
    private String mNote;

    //Post key
    private String post_key;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_income, container, false);

        //progress dialog
        progressDialog = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        mIncomeTotal = view.findViewById(R.id.total_income_tv);
        fetchData();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int totalVal = 0;
                for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                    Data data = mySnapshot.getValue(Data.class);
                    totalVal += data.getAmount();
                    mIncomeTotal.setText(String.valueOf(totalVal));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }

    void fetchData() {

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        //Getting curremt user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        query = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
                    @NonNull
                    @NotNull
                    @Override
                    //Extracting data from real time database
                    public Data parseSnapshot(@NonNull @NotNull DataSnapshot snapshot) {
                        return new Data(Integer.parseInt(snapshot.child("amount").getValue().toString())
                                , snapshot.child("type").getValue().toString()
                                , snapshot.child("note").getValue().toString()
                                , snapshot.child("id").getValue().toString(),
                                snapshot.child("date").getValue().toString());
                    }
                }).build();
        adapter = new FirebaseRecyclerAdapter<Data, ViewHolder>(options) {
            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data, parent, false);
                return new ViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position, @NonNull @NotNull Data model) {
                holder.setAmount(String.valueOf(model.getAmount()));
                holder.setDate(model.getDate());
                holder.setNote(model.getNote());
                holder.setType(model.getType());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        mAmount = model.getAmount();
                        mType = model.getType();
                        mNote = model.getNote();
                        updateDataItem();
                    }
                });
            }
        };
        // setting adapter to recyclerview kept in last so, data is shown
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateDataItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View alertDialogView = inflater.inflate(R.layout.update_data_item, null);
        myDialog.setView(alertDialogView);

        mAmountEt = alertDialogView.findViewById(R.id.amount_et);
        mTypeEt = alertDialogView.findViewById(R.id.type_et);
        mNoteEt = alertDialogView.findViewById(R.id.note_et);

        //Setting data to edit text for update

        mAmountEt.setText(String.valueOf(mAmount));
        mAmountEt.setSelection(String.valueOf(mAmount).length());

        mTypeEt.setText(mType);
        mTypeEt.setSelection(mType.length());

        mNoteEt.setText(mNote);
        mNoteEt.setSelection(mNote.length());

        mUpdatebtn = alertDialogView.findViewById(R.id.update_btn);
        mDeletebtn = alertDialogView.findViewById(R.id.delete_btn);

        AlertDialog dialog = myDialog.create();

        mUpdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = mTypeEt.getText().toString().trim();
                mNote = mNoteEt.getText().toString().trim();
                mAmount = Integer.parseInt(mAmountEt.getText().toString().trim());

                if (TextUtils.isEmpty(mType)) {
                    mTypeEt.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(mNote)) {
                    mNoteEt.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(String.valueOf(mAmount))) {
                    mAmountEt.setError("Required field1!");
                    return;
                }
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mAmount, mType, mNote, post_key, mDate);

                mIncomeDatabase.child(post_key).setValue(data);
                dialog.dismiss();

            }
        });
        mDeletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteItem();
                progressDialog.dismiss();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void deleteItem() {
        try {
            mIncomeDatabase.child(post_key).removeValue();
        } catch (Exception e) {

        }
        Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
    }
}