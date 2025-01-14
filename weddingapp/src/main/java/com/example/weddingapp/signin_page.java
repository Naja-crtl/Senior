package com.example.weddingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin_page extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton;
    private TextView signUpLink;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinpage);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        signUpLink = findViewById(R.id.signUpLink);

        setSignInButtonListener();
        setupSignUpLink();
    }

    private void setSignInButtonListener() {
        signInButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(signin_page.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sign in with Firebase Authentication
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in successful
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(signin_page.this, "Sign-In Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to the next activity
                            Intent intent = new Intent(signin_page.this, couplenames.class);
                            startActivity(intent);
                            finish(); // Prevent going back to the sign-in screen
                        } else {
                            // If sign-in fails, display a message to the user
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign-In Failed";
                            Toast.makeText(signin_page.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setupSignUpLink() {
        String text = "Don't have an Account? Sign Up";
        SpannableString spannable = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Navigate to the sign-up page
                Intent intent = new Intent(signin_page.this, signup_page.class);
                startActivity(intent);
            }
        };

        int signUpStartIndex = text.indexOf("Sign Up");
        spannable.setSpan(clickableSpan, signUpStartIndex, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), signUpStartIndex, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpLink.setText(spannable);
        signUpLink.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }
}
