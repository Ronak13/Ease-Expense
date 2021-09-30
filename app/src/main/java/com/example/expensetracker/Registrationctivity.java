package com.example.expensetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Registrationctivity extends AppCompatActivity {


    private EditText mEmail;
    private EditText mPassword;
    private TextView mSignin;
    private android.widget.Button mSignup;


    private ProgressDialog mDialog;

    // Firebase....
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationctivity);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        mEmail = findViewById(R.id.registraction_email_et);
        mPassword = findViewById(R.id.registraction_pass_et);
        mSignin = findViewById(R.id.signin_tv);
        mSignup = findViewById(R.id.signup_btn);

        registration();
    }

    private void registration() {

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("required field!");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPassword.setError("required field!");
                    return;
                }


                mDialog.setMessage("Processing...");
                mDialog.show();


                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();
                                        Toast.makeText(Registrationctivity.this, "Email has been sent to verify your account!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Registrationctivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(Registrationctivity.this, "User is alreay Registred!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registrationctivity.this, MainActivity.class));
            }
        });
    }

}