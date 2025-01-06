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

import androidx.appcompat.app.AppCompatActivity;

public class signup_page extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, contactNumberEditText;
    private Spinner countryCodeSpinner;
    private Button signUpButton;
    private CheckBox termsCheckBox;
    private TextView signinLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        dbHelper = new DatabaseHelper(this);

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

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contactNumber.isEmpty()) {
                Toast.makeText(signup_page.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(signup_page.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!termsCheckBox.isChecked()) {
                Toast.makeText(signup_page.this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            } else {
                String fullPhoneNumber = countryCode + " " + contactNumber;
                if (dbHelper.insertUser(email, password)) {
                    Toast.makeText(signup_page.this, "Sign-Up Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signup_page.this, signin_page.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(signup_page.this, "Sign-Up Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSignInLink() {
        String text = "Already have an Account? Sign In";
        SpannableString spannable = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
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
