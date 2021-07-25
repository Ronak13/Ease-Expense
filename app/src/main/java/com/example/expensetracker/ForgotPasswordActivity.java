package com.example.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private TextView mSignin;
    private android.widget.Button mforgotbtn;
    private ProgressDialog mDiloag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmail = findViewById(R.id.forgot_email_et);
        mSignin = findViewById(R.id.forgot_signin_tv);
        mforgotbtn = findViewById(R.id.forgotpassword_btn);


        mforgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    mEmail.setError("required field!");
                    return;
                }
                mDiloag = new ProgressDialog(ForgotPasswordActivity.this);
                sentPasswordResetLink(email);

            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
            }
        });
    }

    public void sentPasswordResetLink(String email) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mDiloag.setMessage("Processing!!");
        mDiloag.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                mDiloag.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Password reset link has been sent!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You are not a registered user!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}