package com.example.seniorprojectjan;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEdittxt;
    EditText passswordEditTxt;
    EditText confirmpassEditTxt;
    EditText usernameEditTxt;
    TextView passStatTxtView;
    Button signUpBtn;
    private FirebaseAuth mAuth;
    UserProfileChangeRequest profileUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        mAuth = FirebaseAuth.getInstance();


        emailEdittxt = findViewById(R.id.signUpEmailEditText);
        passswordEditTxt = findViewById(R.id.signUpPasswordEditText);
        confirmpassEditTxt = findViewById(R.id.signUpConfirmPasswordEditText);
        passStatTxtView = findViewById(R.id.passwordStatusTxtView);
        signUpBtn = findViewById(R.id.signUpButtonForm);
        usernameEditTxt = findViewById(R.id.signUpUserEditText);

        //Changes to be done when user is inserting
        emailEdittxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint({"ResourceType", "UseCompatLoadingForColorStateLists"})
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               if (checkEmail(s)!=true) {

                   emailEdittxt.setTextColor(Color.rgb(255,0,0));

               }
               else emailEdittxt.setTextColor(getResources().getColorStateList(R.drawable.edit_text_selector));


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Check pass complexity
        passswordEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (checkIfpassGood(passswordEditTxt.getText().toString())!=true){

                    passStatTxtView.setText("Make sure your password has at least 8 characters, 1 symbol and 1 uppercase letter");
                    passswordEditTxt.setTextColor(Color.RED);

                }
                else
                {

                    passStatTxtView.setText("Password is valid!");
                    passswordEditTxt.setTextColor(Color.GREEN);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Check if pass was confirmed
        confirmpassEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




            }

            @Override
            public void afterTextChanged(Editable s) {

                String password = passswordEditTxt.getText().toString();
                String cpassword = confirmpassEditTxt.getText().toString();

                if(checkIfPassMatch()){

                    confirmpassEditTxt.setTextColor(Color.GREEN);

                }
                else {

                    confirmpassEditTxt.setTextColor(Color.RED);

                }

            }
        });


        //Sign up button functionality
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmail(emailEdittxt.getText()) != true || checkIfpassGood(passswordEditTxt.getText().toString()) != true || checkIfPassMatch()!= true) {

                  /* NEEDS FIXING************************************************
                    AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                    builder.setView(R.layout.custom_dialog);
                    builder.show();
                    ***************************************************************/
                    Toast.makeText(SignUpActivity.this, "Please make sure all information is correct", Toast.LENGTH_SHORT).show();

                } else {

                    //Check for blanks with try catch

                    try {

                        //Creating the user

                        mAuth.createUserWithEmailAndPassword(emailEdittxt.getText().toString(), passswordEditTxt.getText().toString())
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("Sign up stat", "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();



                                            //set his username
                                            profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(usernameEditTxt.getText().toString())
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("Username", "User profile updated.");
                                                                FirebaseDatabase dbreff = FirebaseDatabase.getInstance();
                                                                DatabaseReference root = dbreff.getReference();
                                                                DatabaseReference users = root.child("users");
                                                                dbreff.getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Username").setValue(usernameEditTxt.getText().toString());
                                                            }
                                                        }
                                                    });


                                            Toast toast = Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG);
                                            toast.show();
                                            Log.d("SingInActivity", "signInWithCredential:success");
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            // If sign in fails, display a message to the user.

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
                                            builder.setView(R.layout.custom_dialog);
                                            builder.show();
                                            Toast toast = Toast.makeText(SignUpActivity.this, "Sign up Failed", Toast.LENGTH_LONG);
                                            toast.show();


                                        }


                                    }
                                });
                    }catch (Exception e){


                        Toast.makeText(SignUpActivity.this, "Please Fill all required fields", Toast.LENGTH_SHORT).show();

                    }


                }
            }
        });

    }




    //Check e-mail has @ and .
    private boolean checkEmail(CharSequence s){

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();

    }


    //Check if pass matches the confirmation
    private boolean checkIfPassMatch(){

        String password = passswordEditTxt.getText().toString();
        String cpassword = confirmpassEditTxt.getText().toString();

        if(cpassword.equals(password)){

           return true;

        }
        else {

            return false;

        }


    }


    //Check if pass complexity is good
    private boolean checkIfpassGood(String s)
    {

        if(s.length()<8){
            return false;
        }
        if(  s.matches("(?=.*[0-9]).*") && s.matches("(?=.*[a-z]).*") && s.matches("(?=.*[A-Z]).*")  ) {
            return true;
        }
        else return false;

    }

    @Override
    public void onBackPressed() {

        Intent LoginIntent = new Intent(SignUpActivity.this,LoginPage.class);
        startActivity(LoginIntent);
        finish();


    }


}