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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    Button login;
    String UniCompany, Faculty;
    private TextInputLayout dateLayoutText, layout_uni_company, layout_faculty;
    private TextInputEditText editTextRegisterFullName, editTextRegisterEmail, text_register_dob,
            editTextRegisterPhone, editTextRegisterPwd, editTextRegisterConfirmPwd, textUniCompany, textFaculty;
    private ImageView editTextDateOfBirth;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterRole;
    private DatePickerDialog picker;
    private RadioButton radioButtonRegisterRoleSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterFullName = findViewById(R.id.edt_register_full_name);
        editTextRegisterEmail = findViewById(R.id.edt_register_email);
        editTextRegisterPwd = findViewById(R.id.edt_register_pwd);
        editTextRegisterConfirmPwd = findViewById(R.id.edt_register_confirm_pwd);
        editTextDateOfBirth = findViewById(R.id.edt_register_doB);
        editTextRegisterPhone = findViewById(R.id.edt_register_mobile);
        text_register_dob = findViewById(R.id.result_dob_register);

        dateLayoutText = findViewById(R.id.text_register_dob);

        progressBar = findViewById(R.id.progress_bar);

        radioGroupRegisterRole = findViewById(R.id.radioGroup_registerRole);
        radioGroupRegisterRole.clearCheck();

        layout_uni_company = findViewById(R.id.layout_register_uni_company);
        textFaculty = findViewById(R.id.text_register_faculty);
        textUniCompany = findViewById(R.id.text_register_uni);
        layout_faculty = findViewById(R.id.layout_register_faculty);

        login = findViewById(R.id.loginFromRegister);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        radioGroupRegisterRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonRegisterRoleSelected = findViewById(radioGroupRegisterRole.getCheckedRadioButtonId());
                layout_uni_company.setVisibility(View.VISIBLE);
                textUniCompany.clearComposingText();
                Objects.requireNonNull(textUniCompany.getText()).clear();
                if (radioButtonRegisterRoleSelected.getText().toString().equals("Student")) {
                    textUniCompany.setHint("Write your university name");
                    layout_faculty.setVisibility(View.VISIBLE);
                    textFaculty.clearComposingText();
                    Objects.requireNonNull(textFaculty.getText()).clear();
                    textFaculty.setHint("Enter a faculty you're studying on");
                } else if (radioButtonRegisterRoleSelected.getText().toString().equals("HR Specialist")) {
                    layout_faculty.setVisibility(View.GONE);
                    textFaculty.clearComposingText();
                    Objects.requireNonNull(textFaculty.getText()).clear();
                    textUniCompany.setHint("Write a company you're working in");
                }
            }
        });


        editTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateLayoutText.setVisibility(View.VISIBLE);
                        text_register_dob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
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
                String textDoB = text_register_dob.getText().toString();
                String textMobile = editTextRegisterPhone.getText().toString();
                String textPassword = editTextRegisterPwd.getText().toString();
                String textConfirmPassword = editTextRegisterConfirmPwd.getText().toString();
                String textRole;

                String mobileRegex = "[5][0-9]{7}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);


                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                    text_register_dob.setError("Date of birth is required");
                    editTextDateOfBirth.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Phone number is required");
                    editTextRegisterPhone.requestFocus();
                } else if (textMobile.length() != 9) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Please enter valid phone number");
                    editTextRegisterPhone.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Phone number is not valid", Toast.LENGTH_LONG).show();
                    editTextRegisterPhone.setError("Not a valid phone number");
                    editTextRegisterPhone.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password has to be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is to weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password and confirm password doesn't match", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else if (radioGroupRegisterRole.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your role", Toast.LENGTH_LONG).show();
                    radioGroupRegisterRole.requestFocus();
                } else {
                    textRole = radioButtonRegisterRoleSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    if (textRole.equals("Student")) {
                        UniCompany = textUniCompany.getText().toString();
                        Faculty = textFaculty.getText().toString();
                        registerUser(textFullName, textEmail, textDoB, textRole, "+972" + textMobile, textPassword, UniCompany, Faculty);
                    } else if (textRole.equals("HR Specialist")) {
                        UniCompany = textUniCompany.getText().toString();
                        registerUser(textFullName, textEmail, textDoB, textRole, "+972" + textMobile, textPassword, UniCompany);

                    }
                }
            }
        });

    }


    private void registerUser(String textFullName, String textEmail, String textDoB, String textRole, String textMobile, String textPassword, String UniCompany) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textRole, textMobile, UniCompany);
                            DatabaseReference referenceProfile;


                            referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs");


                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "Registration successful. Please verify your email", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed, try again", Toast.LENGTH_LONG).show();
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

    private void registerUser(String textFullName, String textEmail, String textDoB, String textRole, String textMobile, String textPassword, String UniCompany, String Faculty) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);


                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB, textRole, textMobile, UniCompany, Faculty);



                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        firebaseUser.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "Registration successful. Please verify your email", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed, try again", Toast.LENGTH_LONG).show();
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