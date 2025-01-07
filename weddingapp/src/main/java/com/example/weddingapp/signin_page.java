package com.example.weddingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class signin_page extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton;
    private TextView signUpLink;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinpage);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
                            if (user != null) {
                                fetchCoupleData(user.getUid()); // Fetch couple data under the signed-in user
                            }
                        } else {
                            // If sign-in fails, display a message to the user
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Sign-In Failed";
                            Toast.makeText(signin_page.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void fetchCoupleData(String userId) {
        // Reference the user's `couples` subcollection
        CollectionReference couplesRef = firestore.collection("users").document(userId).collection("couples");

        couplesRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Get the first document (assuming one set of couple data per user)
                        DocumentSnapshot coupleDoc = querySnapshot.getDocuments().get(0);

                        String partner1 = coupleDoc.getString("partner1");
                        String partner2 = coupleDoc.getString("partner2");
                        String weddingDate = coupleDoc.getString("weddingDate");

                        // Navigate to the dashboard with the retrieved data
                        Intent intent = new Intent(signin_page.this, dashboard.class);
                        intent.putExtra("partner1", partner1);
                        intent.putExtra("partner2", partner2);
                        intent.putExtra("weddingDate", weddingDate);
                        startActivity(intent);
                        finish();
                    } else {
                        // No couple data found; navigate to the couple names page
                        Intent intent = new Intent(signin_page.this, couplenames.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(signin_page.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setupSignUpLink() {
        signUpLink.setOnClickListener(v -> {
            // Navigate to the sign-up page
            Intent intent = new Intent(signin_page.this, signup_page.class);
            startActivity(intent);
        });
    }
}
