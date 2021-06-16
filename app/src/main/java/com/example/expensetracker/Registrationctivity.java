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

        mDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        registration();
    }

    private void registration() {
        mEmail = findViewById(R.id.registraction_email_et);
        mPassword = findViewById(R.id.registraction_pass_et);
        mSignin = findViewById(R.id.signin_tv);
        mSignup = findViewById(R.id.signup_btn);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required field!!");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPassword.setError("Password is requred field!");
                    return;
                }


                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Toast.makeText(Registrationctivity.this, "Registration completed successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(Registrationctivity.this, "Registration is failed!", Toast.LENGTH_SHORT).show();
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