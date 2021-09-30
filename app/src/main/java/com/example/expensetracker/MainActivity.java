package com.example.expensetracker;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 11;
    private EditText mEmail;
    private EditText mPassword;
    private TextView mSignup;
    private TextView mForgotPass;
    private android.widget.Button mLogin;
    private ProgressDialog mDialog;
    //firebase
    private FirebaseAuth mAuth;
    // Google sign in
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mSignInButton;
    private GoogleSignInAccount acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acc = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();

        // to check if user has already login or not
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified() && acc != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
        mDialog = new ProgressDialog(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // button
        mSignInButton = findViewById(R.id.gsi_btn);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginDetails();
    }

    public void loginDetails() {

        mEmail = findViewById(R.id.email_et);
        mPassword = findViewById(R.id.pass_et);
        mLogin = findViewById(R.id.login_btn);
        mForgotPass = findViewById(R.id.forgotpass_tv);
        mSignup = findViewById(R.id.signup_tv);


        AlphaAnimation btnclick = new AlphaAnimation(0F, 1.8F);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setAnimation(btnclick);
                String email = mEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("required field!");
                    mEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPassword.setError("required field!");
                    mPassword.requestFocus();
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please verify your email before login!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, Registrationctivity.class));
                            Toast.makeText(getApplicationContext(), " either user is not registred or check your credentials", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        // FOr signup
        mSignup.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Registrationctivity.class);
            startActivity(i);
        });

        // For forgot passowrd
        mForgotPass.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class)));
    }


    //signin()
    void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d(TAG, "OnActitvity resuly" + e.getMessage());

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mDialog.setMessage("Processing");
        mDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}