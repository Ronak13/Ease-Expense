package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class DashboardFragment extends Fragment {

    //floating button

    private FloatingActionButton fab_main;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;


    //Floating button textview

    private TextView fab_income_tv;
    private TextView fab_expense_tv;


    private boolean isOpen = false;

    // Animatopn fade in / out

    private Animation fadeOpen, fadeClose;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUsr;
    private DatabaseReference mIncomeDatabse;
    private DatabaseReference mExpenseDatabase;
    private Query incomeQuery;
    private Query expenseQuery;
    private FirebaseRecyclerAdapter mIncomeAdapter;
    private FirebaseRecyclerAdapter mExpenseAdapter;
    private String uid;

    //Total epense and income textview
    private TextView mTotalIncome;
    private TextView mTotalExpense;


    //Recyclerview for income and expense

    private RecyclerView mIncomeRecycler;
    private RecyclerView mExpenseRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUsr = mAuth.getCurrentUser();
        uid = mFirebaseUsr.getUid();

        mIncomeDatabse = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        // Fab button to layout
        fab_main = view.findViewById(R.id.fab_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_ft_btn);

        // Income and Expense Textviews

        fab_income_tv = view.findViewById(R.id.income_ft_tv);
        fab_expense_tv = view.findViewById(R.id.expense_ft_tv);

        //Total income and expense result
        mTotalExpense = view.findViewById(R.id.total_expense_dashboard_tv);
        mTotalIncome = view.findViewById(R.id.total_income_dashboard_tv);

        //Recycler
        mIncomeRecycler = view.findViewById(R.id.dashboard_income_recycler);
        mExpenseRecycler = view.findViewById(R.id.dashboard_expense_recycler);


        //Animation connect
        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(fadeClose);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_tv.startAnimation(fadeClose);
                    fab_expense_tv.startAnimation(fadeClose);
                    fab_income_tv.setClickable(false);
                    fab_expense_tv.setClickable(false);

                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(fadeOpen);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_tv.startAnimation(fadeOpen);
                    fab_expense_tv.startAnimation(fadeOpen);
                    fab_income_tv.setClickable(true);
                    fab_expense_tv.setClickable(true);

                    isOpen = true;
                }
            }
        });

        mIncomeDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int totalIncome = 0;
                for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                    Data data = mSnapshot.getValue(Data.class);
                    totalIncome += data.getAmount();
                    mTotalIncome.setText(String.valueOf(totalIncome));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
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

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mIncomeRecycler.setHasFixedSize(true);
        mIncomeRecycler.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mExpenseRecycler.setHasFixedSize(true);
        mExpenseRecycler.setLayoutManager(layoutManagerExpense);
        fetchIncomeData();
        fetchExpenseData();
        return view;

    }

    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_tv.startAnimation(fadeClose);
            fab_expense_tv.startAnimation(fadeClose);
            fab_income_tv.setClickable(false);
            fab_expense_tv.setClickable(false);

            isOpen = false;
        } else {
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_tv.startAnimation(fadeOpen);
            fab_expense_tv.startAnimation(fadeOpen);
            fab_income_tv.setClickable(true);
            fab_expense_tv.setClickable(true);

            isOpen = true;
        }
    }

    private void addData() {
        //Fab income button action

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.custom_layout_for_intersting_data, null);
        mydialog.setView(v);

        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText etAmount = v.findViewById(R.id.amount_et);
        EditText etType = v.findViewById(R.id.type_et);
        EditText etNote = v.findViewById(R.id.note_et);

        android.widget.Button btnSave = v.findViewById(R.id.save_btn);
        android.widget.Button btnCancel = v.findViewById(R.id.cancel_btn);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = etAmount.getText().toString().trim();
                String note = etNote.getText().toString().trim();
                String type = etType.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    etAmount.setError("Required Field!!");
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    etType.setError("Required Field!!");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    etNote.setError("Required Field!!");
                    return;
                }
                int intAmount = Integer.parseInt(amount);

                String id = mIncomeDatabse.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intAmount, type, note, id, mDate);
                mIncomeDatabse.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added!!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.custom_layout_for_intersting_data, null);
        mydialog.setView(v);

        AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);


        EditText etAmount = v.findViewById(R.id.amount_et);
        EditText etType = v.findViewById(R.id.type_et);
        EditText etNote = v.findViewById(R.id.note_et);


        android.widget.Button btnSave = v.findViewById(R.id.save_btn);
        android.widget.Button btncancel = v.findViewById(R.id.cancel_btn);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = etAmount.getText().toString().trim();
                String type = etType.getText().toString().trim();
                String note = etNote.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    etAmount.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(type)) {
                    etAmount.setError("Required field!!");
                    return;
                }
                if (TextUtils.isEmpty(note)) {
                    etAmount.setError("Required field!!");
                    return;
                }
                int intAmount = Integer.parseInt(amount);

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intAmount, type, note, id, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data added!!", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void fetchIncomeData() {
        incomeQuery = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(incomeQuery, new SnapshotParser<Data>() {
                    @NonNull
                    @NotNull
                    @Override
                    public Data parseSnapshot(@NonNull @NotNull DataSnapshot snapshot) {
                        return new Data(Integer.parseInt(snapshot.child("amount").getValue().toString())
                                , snapshot.child("type").getValue().toString()
                                , snapshot.child("note").getValue().toString()
                                , snapshot.child("id").getValue().toString()
                                , snapshot.child("date").getValue().toString());
                    }
                }).build();
        mIncomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {

            @NonNull
            @NotNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull IncomeViewHolder holder, int position, @NonNull @NotNull Data model) {
                holder.setAmount(String.valueOf(model.getAmount()));
                holder.setDate(model.getDate());
                holder.setType(model.getType());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "for detail information go to income tab", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mIncomeRecycler.setAdapter(mIncomeAdapter);
        mIncomeAdapter.startListening();

    }

    private void fetchExpenseData() {
        expenseQuery = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(expenseQuery, new SnapshotParser<Data>() {
                    @NonNull
                    @NotNull
                    @Override
                    public Data parseSnapshot(@NonNull @NotNull DataSnapshot snapshot) {
                        return new Data(
                                Integer.parseInt(snapshot.child("amount").getValue().toString())
                                , snapshot.child("type").getValue().toString()
                                , snapshot.child("note").getValue().toString()
                                , snapshot.child("id").getValue().toString()
                                , snapshot.child("date").getValue().toString());
                    }
                }).build();
        mExpenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options) {

            @NonNull
            @NotNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull ExpenseViewHolder holder, int position, @NonNull @NotNull Data model) {
                holder.setAmount(String.valueOf(model.getAmount()));
                holder.setType(model.getType());
                holder.setDate(model.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "for detail information go to expense tab", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mExpenseRecycler.setAdapter(mExpenseAdapter);
        mExpenseAdapter.startListening();
    }
}