package com.example.seniorprojectjan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {


    private static final int RC_SIGN_IN = 120;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private Button signUpBtn;
    private Button signInBtn;
    private EditText emailEditTxt;
    private EditText passwordEditTxt;
    private TextView loginStatus;
    private String email;
    private String password;
    private Activity thisActivity;

    FirebaseDatabase FDb = FirebaseDatabase.getInstance();
    DatabaseReference users = FDb.getReference().child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        thisActivity = LoginPage.this;

        //initializing sign in auth
        mAuth = FirebaseAuth.getInstance();

        //Sign up button functionality
        signUpBtn = (Button) findViewById(R.id.signUpButton);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginPage.this,SignUpActivity.class);
                startActivity(signUpIntent);
                finish();


            }
        });





        //THIS PART IS E-MAIL/PASSWORD SIGN IN RELATED*******************************************************
        emailEditTxt = findViewById(R.id.signEmailEditText);
        passwordEditTxt = findViewById(R.id.signPasswordEditText);
        signInBtn = findViewById(R.id.signInBtnLoginPage);
        signInBtn.setOnClickListener(new View.OnClickListener() {
                                         @RequiresApi(api = Build.VERSION_CODES.P)
                                         @Override
                                         public void onClick(View v) {

                                             try {

                                                 email = emailEditTxt.getText().toString();
                                                 password = passwordEditTxt.getText().toString();
                                                 mAuth.signInWithEmailAndPassword(email, password)
                                                         .addOnCompleteListener(thisActivity, new OnCompleteListener<AuthResult>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<AuthResult> task) {
                                                                 if (task.isSuccessful()) {
                                                                     // Sign in success, update UI with the signed-in user's information
                                                                     FirebaseUser user = mAuth.getCurrentUser();
                                                                     Toast toast = Toast.makeText(LoginPage.this, "Sign up successful!", Toast.LENGTH_LONG);
                                                                     toast.show();
                                                                     Log.d("SingInActivity", "signInWithCredential:success");
                                                                     Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                                                     startActivity(intent);
                                                                     finish();
                                                                 } else {
                                                                     // If sign in fails, display a message to the user.

                                                                     Toast.makeText(LoginPage.this, "Authentication failed.",
                                                                             Toast.LENGTH_SHORT).show();
                                                                     loginStatus = LoginPage.this.findViewById(R.id.loginStatus);
                                                                     loginStatus.setText("Login Failed!");
                                                                     loginStatus.setTextColor(Color.RED);
                                                                     // ...
                                                                 }

                                                                 // ...
                                                             }
                                                         });
                                             }catch (Exception e)
                                             {
                                                 Toast.makeText(thisActivity, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                                                 emailEditTxt.setHintTextColor(Color.parseColor("#fd5656"));
                                                 passwordEditTxt.setHintTextColor(Color.parseColor("#fd5656"));


                                             }


                                         }




    });











        //THIS PART IS GOOGLE SIGN IN RELATED****************************************************************
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        SignInButton sign_in_button = this.findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if(task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("sign in activity", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());


                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("sign in activity", "Google sign in failed", e);
                    // ...
                }
            }else {

                Log.w("sign in activity", exception.toString());
                  }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SingInActivity", "signInWithCredential:success");
                            users.child(mAuth.getCurrentUser().getUid()).child("Username").setValue(mAuth.getCurrentUser().getDisplayName());
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SingInActivity", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


}