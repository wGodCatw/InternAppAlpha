package com.example.internapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivityV2 extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextDateOfBirth,
            editTextRegisterPhone, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterRole;

    Button login;
    private DatePickerDialog picker;
    private RadioButton radioButtonRegisterRoleSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v2);

        editTextRegisterFullName = findViewById(R.id.edt_register_full_name);
        editTextRegisterEmail = findViewById(R.id.edt_register_email);
        editTextRegisterPwd = findViewById(R.id.edt_register_password);
        editTextDateOfBirth = findViewById(R.id.edt_register_dob);
        editTextRegisterConfirmPwd = findViewById(R.id.edt_register_confirm_password);
        editTextRegisterPhone = findViewById(R.id.edt_register_mobile);

        progressBar = findViewById(R.id.progress_bar);

        radioGroupRegisterRole = findViewById(R.id.radioGroup_registerRole);
        radioGroupRegisterRole.clearCheck();

        login = findViewById(R.id.loginFromRegister);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivityV2.this, LoginActivityV2.class);
                startActivity(intent);
                finish();
            }
        });
        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivityV2.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRoleId = radioGroupRegisterRole.getCheckedRadioButtonId();
                radioButtonRegisterRoleSelected = findViewById(selectedRoleId);


                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextDateOfBirth.getText().toString();
                String textMobile = editTextRegisterPhone.getText().toString();
                String textPassword = editTextRegisterPwd.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPwd.getText().toString();
                String textRole;

                String mobileRegex = "[0][5][0247][0-9]{6}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);





                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivityV2.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivityV2.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivityV2.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivityV2.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextDateOfBirth.setError("Date of birth is required");
                    editTextDateOfBirth.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivityV2.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Phone number is required");
                    editTextRegisterPhone.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(RegisterActivityV2.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Please enter valid phone number");
                    editTextRegisterPhone.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivityV2.this, "Phone number is not valid", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Not a valid phone number");
                    editTextRegisterPhone.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivityV2.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(RegisterActivityV2.this, "Password has to be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is to weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(RegisterActivityV2.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(RegisterActivityV2.this, "Password and confirm password doesn't match", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else if (radioGroupRegisterRole.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivityV2.this, "Please select your role", Toast.LENGTH_LONG).show();
                    radioGroupRegisterRole.requestFocus();
                } else {
                    textRole = radioButtonRegisterRoleSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textRole, textMobile, textPassword);
                }
            }
        });

    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textRole, String textMobile, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivityV2.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivityV2.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textRole, textMobile);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivityV2.this, "Registration successful. Please verify your email", Toast.LENGTH_LONG).show();

//                                        Intent intent = new Intent(RegisterActivityV2.this, UserProfile.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivityV2.this, "Registration failed, try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });


                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextRegisterEmail.setError("Your email is invalid or already in use, re-enter your email");
                                editTextRegisterEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editTextRegisterEmail.setError("User already registered, Log in or use a different email");
                                editTextRegisterEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, Objects.requireNonNull(e.getMessage()));

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}