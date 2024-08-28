package com.example.internapp.MainDir;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowCompat;

import com.example.internapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Activity for user registration.
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private final String[] universities = {"Tel Aviv", "Bar-Ilan", "Ben Gurion", "Technion", "Haifa", "Weizmann", "Reichman", "Hebrew", "Open University", "Ariel"};
    private final String[] faculties = {"Business", "Health", "Social sciences", "Engineering", "Psychology", "Computer science", "Education", "Journalism", "Biology", "Visual arts"};
    Button login;
    String UniCompany, Faculty;
    private TextInputLayout dateLayoutText, layout_uni_company;
    private TextInputEditText editTextRegisterFullName, editTextRegisterEmail, text_register_dob, editTextRegisterPhone, editTextRegisterPwd, editTextRegisterConfirmPwd, textUniCompany, edt_username;
    private ImageView editTextDateOfBirth;
    private ProgressBar progressBar;
    private AutoCompleteTextView autoUniversity, autoFaculty;
    private CoordinatorLayout layout_autoUni, layout_autoFaculty;
    private RadioGroup radioGroupRegisterRole;
    private DatePickerDialog picker;
    private RadioButton radioButtonRegisterRoleSelected;

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_register);

        ScrollView scrollView = findViewById(R.id.parentScroll);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        // Initializing UI elements
        editTextRegisterFullName = findViewById(R.id.edt_register_full_name);
        editTextRegisterEmail = findViewById(R.id.edt_register_email);
        editTextRegisterPwd = findViewById(R.id.edt_register_pwd);
        editTextRegisterConfirmPwd = findViewById(R.id.edt_register_confirm_pwd);
        editTextDateOfBirth = findViewById(R.id.edt_register_doB);
        editTextRegisterPhone = findViewById(R.id.edt_register_mobile);
        text_register_dob = findViewById(R.id.result_dob_register);
        edt_username = findViewById(R.id.edt_username);
        layout_autoUni = findViewById(R.id.layout_coordinator_uni);
        layout_autoFaculty = findViewById(R.id.layout_coordinator_faculty);
        dateLayoutText = findViewById(R.id.text_register_dob);
        progressBar = findViewById(R.id.progress_bar);
        radioGroupRegisterRole = findViewById(R.id.radioGroup_registerRole);
        layout_uni_company = findViewById(R.id.layout_register_uni_company);
        textUniCompany = findViewById(R.id.text_register_uni);
        autoUniversity = findViewById(R.id.uniSelect);
        autoFaculty = findViewById(R.id.facultySelect);

        // Populating AutoCompleteTextViews
        ArrayAdapter<String> adapterUnis = new ArrayAdapter<>(this, R.layout.faculty_item, universities);
        ArrayAdapter<String> adapterFaculties = new ArrayAdapter<>(this, R.layout.faculty_item, faculties);
        autoUniversity.setAdapter(adapterUnis);
        autoFaculty.setAdapter(adapterFaculties);

        autoUniversity.setOnItemClickListener((parent, view, position, id) -> {
            // Additional handling if needed
        });

        autoFaculty.setOnItemClickListener((parent, view, position, id) -> {
            // Additional handling if needed
        });

        login = findViewById(R.id.loginFromRegister);
        login.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        radioGroupRegisterRole.setOnCheckedChangeListener((group, checkedId) -> {
            radioButtonRegisterRoleSelected = findViewById(radioGroupRegisterRole.getCheckedRadioButtonId());
            layout_uni_company.setVisibility(View.VISIBLE);
            textUniCompany.clearComposingText();
            Objects.requireNonNull(textUniCompany.getText()).clear();
            if (radioButtonRegisterRoleSelected.getText().toString().equals("Student")) {
                layout_uni_company.setVisibility(View.GONE);
                layout_autoUni.setVisibility(View.VISIBLE);
                layout_autoFaculty.setVisibility(View.VISIBLE);
                layout_uni_company.setVisibility(View.GONE);
                layout_autoUni.setVisibility(View.VISIBLE);
                layout_autoFaculty.setVisibility(View.VISIBLE);
            } else if (radioButtonRegisterRoleSelected.getText().toString().equals("HR Specialist")) {
                layout_autoFaculty.setVisibility(View.GONE);
                layout_autoUni.setVisibility(View.GONE);
                layout_uni_company.setVisibility(View.VISIBLE);
            }
        });

        editTextDateOfBirth.setOnClickListener(v -> {
            // Display date picker dialog
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            picker = new DatePickerDialog(RegisterActivity.this, (view, year1, month1, dayOfMonth) -> {
                dateLayoutText.setVisibility(View.VISIBLE);
                text_register_dob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }, year, month, day);
            picker.show();
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(v -> {
            // Register button click handling
            String textUsername = Objects.requireNonNull(edt_username.getText()).toString().trim();

            findSameUsernameHR(textUsername, isExist -> {
                if (isExist) {
                    // Notify user about existing username
                    Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                    edt_username.setError("Username already exists");
                    edt_username.requestFocus();
                } else {
                    findSameUsernameStudent(textUsername, isExist1 -> {
                        if (isExist1) {
                            // Notify user about existing username
                            Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                            edt_username.setError("Username already exists");
                            edt_username.requestFocus();
                        } else {
                            // Proceed with registration
                            int selectedRoleId = radioGroupRegisterRole.getCheckedRadioButtonId();
                            radioButtonRegisterRoleSelected = findViewById(selectedRoleId);
                            String textFullName = Objects.requireNonNull(editTextRegisterFullName.getText()).toString();
                            String textEmail = Objects.requireNonNull(editTextRegisterEmail.getText()).toString();
                            String textDoB = Objects.requireNonNull(text_register_dob.getText()).toString();
                            String textMobile = Objects.requireNonNull(editTextRegisterPhone.getText()).toString();
                            String textPassword = Objects.requireNonNull(editTextRegisterPwd.getText()).toString();
                            String textConfirmPassword = Objects.requireNonNull(editTextRegisterConfirmPwd.getText()).toString();
                            String textRole;
                            Matcher mobileMatcher;

                            // Validating user input
                            if (TextUtils.isEmpty(textFullName)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                                editTextRegisterFullName.setError("Full name is required");
                                editTextRegisterFullName.requestFocus();
                            } else if (TextUtils.isEmpty(textUsername)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please re-enter your username", Toast.LENGTH_LONG).show();
                                editTextRegisterFullName.setError("Username is too short");
                                editTextRegisterFullName.requestFocus();
                            } else if (textUsername.length() < 6) {
                                // Notify user about username length requirement
                                Toast.makeText(RegisterActivity.this, "Username must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                edt_username.setError("Username is too short");
                                edt_username.requestFocus();
                            } else if (textUsername.contains(" ") || textUsername.contains("&") || textUsername.contains("#")) {
                                // Notify user about disallowed characters in username
                                Toast.makeText(RegisterActivity.this, "Username cannot contain special characters", Toast.LENGTH_LONG).show();
                                edt_username.setError("Username cannot contain special characters");
                                edt_username.requestFocus();
                            } else if (TextUtils.isEmpty(textEmail)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                                editTextRegisterEmail.setError("Email is required");
                                editTextRegisterEmail.requestFocus();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                                // Notify user about invalid email format
                                Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                                editTextRegisterEmail.setError("Valid email is required");
                                editTextRegisterEmail.requestFocus();
                            } else if (TextUtils.isEmpty(textDoB)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                                text_register_dob.setError("Date of birth is required");
                                editTextDateOfBirth.requestFocus();
                            } else if (TextUtils.isEmpty(textMobile)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please enter your phone number", Toast.LENGTH_LONG).show();
                                editTextRegisterPhone.setError("Phone number is required");
                                editTextRegisterPhone.requestFocus();
                            } else if (textMobile.length() < 11 || textMobile.length() > 13) {
                                // Notify user about invalid phone number length
                                Toast.makeText(RegisterActivity.this, "Please re-enter your phone number", Toast.LENGTH_LONG).show();
                                editTextRegisterPhone.setError("Please enter valid phone number");
                                editTextRegisterPhone.requestFocus();
                            } else if (TextUtils.isEmpty(textPassword)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                                editTextRegisterPwd.setError("Password is required");
                                editTextRegisterPwd.requestFocus();
                            } else if (textPassword.length() < 6) {
                                // Notify user about weak password
                                Toast.makeText(RegisterActivity.this, "Password has to be at least 6 digits", Toast.LENGTH_LONG).show();
                                editTextRegisterPwd.setError("Password is too weak");
                                editTextRegisterPwd.requestFocus();
                            } else if (TextUtils.isEmpty(textConfirmPassword)) {
                                // Notify user about empty field
                                Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                                editTextRegisterConfirmPwd.requestFocus();
                            } else if (!textPassword.equals(textConfirmPassword)) {
                                // Notify user about password mismatch
                                Toast.makeText(RegisterActivity.this, "Password and confirm password don't match", Toast.LENGTH_LONG).show();
                                editTextRegisterConfirmPwd.setError("Password confirmation is required");
                                editTextRegisterConfirmPwd.requestFocus();
                                editTextRegisterPwd.clearComposingText();
                                editTextRegisterConfirmPwd.clearComposingText();
                            } else if (radioGroupRegisterRole.getCheckedRadioButtonId() == -1) {
                                // Notify user about role selection requirement
                                Toast.makeText(RegisterActivity.this, "Please select your role", Toast.LENGTH_LONG).show();
                                radioGroupRegisterRole.requestFocus();
                            } else {
                                // User input is valid, proceed with registration
                                textRole = radioButtonRegisterRoleSelected.getText().toString();
                                progressBar.setVisibility(View.VISIBLE);

                                if (textRole.equals("Student")) {
                                    if (TextUtils.isEmpty(autoUniversity.getText())) {
                                        // Notify user about empty field
                                        Toast.makeText(RegisterActivity.this, "Please specify which university you're studying in", Toast.LENGTH_LONG).show();
                                        autoUniversity.requestFocus();
                                        autoUniversity.setError("University not specified");
                                    } else if (TextUtils.isEmpty(autoFaculty.getText())) {
                                        // Notify user about empty field
                                        Toast.makeText(RegisterActivity.this, "Please specify which faculty you're studying in", Toast.LENGTH_LONG).show();
                                        autoFaculty.requestFocus();
                                        autoFaculty.setError("Faculty not specified");
                                    } else {
                                        // Proceed with student registration
                                        UniCompany = autoUniversity.getText().toString();
                                        Faculty = autoFaculty.getText().toString();
                                        registerUser(textUsername.trim(), textFullName.trim(), textEmail, textDoB, textRole, textMobile, textPassword, UniCompany, Faculty);
                                    }
                                }
                                if (textRole.equals("HR Specialist")) {
                                    if (TextUtils.isEmpty(textUniCompany.getText())) {
                                        // Notify user about empty field
                                        Toast.makeText(RegisterActivity.this, "Please specify which company you're working in", Toast.LENGTH_LONG).show();
                                        textUniCompany.requestFocus();
                                        textUniCompany.setError("Company not specified");
                                    } else {
                                        // Proceed with HR specialist registration
                                        UniCompany = textUniCompany.getText().toString();
                                        registerUser(textUsername.trim(), textFullName.trim(), textEmail, textDoB, textRole, "+972" + textMobile, textPassword, UniCompany);
                                    }
                                }
                            }
                        }
                    });
                }
            });
        });
    }


    private void findSameUsernameHR(String textUsername, CustomCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        reference.orderByChild("username").equalTo(textUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCallback(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    /**
     * Searches for a username in the Students registered users.
     *
     * @param textUsername The username to search for.
     * @param callback     Callback interface to handle the result.
     */
    private void findSameUsernameStudent(String textUsername, CustomCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        reference.orderByChild("username").equalTo(textUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCallback(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    /**
     * Registers a user.
     *
     * @param textUsername The username of the user.
     * @param textFullName The full name of the user.
     * @param textEmail    The email of the user.
     * @param textDoB      The date of birth of the user.
     * @param textRole     The role of the user.
     * @param textMobile   The mobile number of the user.
     * @param textPassword The password of the user.
     * @param UniCompany   The company or university of the user.
     */
    private void registerUser(String textUsername, String textFullName, String textEmail, String textDoB, String textRole, String textMobile, String textPassword, String UniCompany) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                FirebaseUser firebaseUser = auth.getCurrentUser();

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                assert firebaseUser != null;
                firebaseUser.updateProfile(profileChangeRequest);

                String userPic = "";
                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textUsername, textFullName, textDoB, textRole, textMobile, UniCompany, userPic);
                DatabaseReference referenceProfile;

                referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs");

                referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {
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
        });
    }

    /**
     * Registers a user with additional faculty information.
     *
     * @param textUsername The username of the user.
     * @param textFullName The full name of the user.
     * @param textEmail    The email of the user.
     * @param textDoB      The date of birth of the user.
     * @param textRole     The role of the user.
     * @param textMobile   The mobile number of the user.
     * @param textPassword The password of the user.
     * @param UniCompany   The company or university of the user.
     * @param Faculty      The faculty information of the user.
     */
    private void registerUser(String textUsername, String textFullName, String textEmail, String textDoB, String textRole, String textMobile, String textPassword, String UniCompany, String Faculty) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                FirebaseUser firebaseUser = auth.getCurrentUser();

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                assert firebaseUser != null;
                firebaseUser.updateProfile(profileChangeRequest);

                Toast.makeText(RegisterActivity.this, textFullName, Toast.LENGTH_LONG).show();
                String userPic = "none";
                ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textUsername, textFullName, textDoB, textRole, textMobile, UniCompany, Faculty, userPic);

                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");

                referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {
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
        });
    }

    public interface CustomCallback {
        void onCallback(Boolean isExist);
    }
}