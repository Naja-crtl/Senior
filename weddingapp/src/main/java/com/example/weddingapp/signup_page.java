package com.example.weddingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class signup_page extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, contactNumberEditText;
    private Spinner countryCodeSpinner;
    private Button signUpButton;
    private CheckBox termsCheckBox;
    private TextView signinLink;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        contactNumberEditText = findViewById(R.id.contactNumber);
        countryCodeSpinner = findViewById(R.id.countryCode);
        signUpButton = findViewById(R.id.signUpButton);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        signinLink = findViewById(R.id.signinlink);

        setupCountryCodeSpinner();
        setSignUpButtonListener();
        setupSignInLink();
    }

    private void setupCountryCodeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.country_codes,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryCodeSpinner.setAdapter(adapter);
    }

    private void setSignUpButtonListener() {
        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String contactNumber = contactNumberEditText.getText().toString().trim();
            String countryCode = countryCodeSpinner.getSelectedItem().toString();

            // Input validation
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contactNumber.isEmpty()) {
                Toast.makeText(signup_page.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(signup_page.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(signup_page.this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            } else {
                String fullPhoneNumber = countryCode + " " + contactNumber;

                // Sign up the user with Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Sign-up successful
                                Toast.makeText(signup_page.this, "Sign-Up Successful", Toast.LENGTH_SHORT).show();

                                // Navigate to Sign-In page
                                Intent intent = new Intent(signup_page.this, signin_page.class);
                                startActivity(intent);
                                finish(); // Prevent returning to sign-up screen
                            } else {
                                // Handle errors during sign-up
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(signup_page.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign-Up Failed";
                                    Toast.makeText(signup_page.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void setupSignInLink() {
        String text = "Already have an Account? Sign In";
        SpannableString spannable = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Navigate to the Sign-In page
                Intent intent = new Intent(signup_page.this, signin_page.class);
                startActivity(intent);
            }
        };

        int signInStartIndex = text.indexOf("Sign In");
        spannable.setSpan(clickableSpan, signInStartIndex, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), signInStartIndex, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signinLink.setText(spannable);
        signinLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }
}
