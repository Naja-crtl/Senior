package com.example.weddingapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    private EditText etPartner1, etPartner2;
    private Button btnWeddingDate, btnSaveProfile;

    private String selectedWeddingDate = "";
    private String documentId = "";

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        etPartner1 = findViewById(R.id.etPartner1);
        etPartner2 = findViewById(R.id.etPartner2);
        btnWeddingDate = findViewById(R.id.btnWeddingDate);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Load existing data
        loadProfileData();

        // Handle wedding date selection
        btnWeddingDate.setOnClickListener(v -> showDatePickerDialog());

        // Save updated profile data
        btnSaveProfile.setOnClickListener(v -> saveProfileData());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    month1++; // Month is 0-indexed
                    selectedWeddingDate = dayOfMonth + "/" + month1 + "/" + year1;
                    btnWeddingDate.setText(selectedWeddingDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadProfileData() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("couples")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        documentId = document.getId();

                        // Populate UI with data
                        etPartner1.setText(document.getString("partner1"));
                        etPartner2.setText(document.getString("partner2"));
                        selectedWeddingDate = document.getString("weddingDate");
                        if (selectedWeddingDate != null) {
                            btnWeddingDate.setText(selectedWeddingDate);
                        }

                        Toast.makeText(profile.this, "Data loaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(profile.this, "No data found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("profile", "Failed to load data: " + e.getMessage());
                    Toast.makeText(profile.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileData() {
        String partner1 = etPartner1.getText().toString().trim();
        String partner2 = etPartner2.getText().toString().trim();

        if (partner1.isEmpty() || partner2.isEmpty() || selectedWeddingDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> coupleData = new HashMap<>();
        coupleData.put("partner1", partner1);
        coupleData.put("partner2", partner2);
        coupleData.put("weddingDate", selectedWeddingDate);

        if (!documentId.isEmpty()) {
            // Update existing document
            firestore.collection("users").document(userId)
                    .collection("couples").document(documentId)
                    .update(coupleData)
                    .addOnSuccessListener(aVoid -> {
                        updateSharedPreferences(partner1, partner2, selectedWeddingDate);
                        Toast.makeText(profile.this, "Data updated successfully!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    })
                    .addOnFailureListener(e -> Toast.makeText(profile.this, "Failed to update data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Add new document
            firestore.collection("users").document(userId)
                    .collection("couples")
                    .add(coupleData)
                    .addOnSuccessListener(documentReference -> {
                        documentId = documentReference.getId();
                        updateSharedPreferences(partner1, partner2, selectedWeddingDate);
                        Toast.makeText(profile.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    })
                    .addOnFailureListener(e -> Toast.makeText(profile.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    // Method to update SharedPreferences
    private void updateSharedPreferences(String partner1, String partner2, String weddingDate) {
        SharedPreferences sharedPreferences = getSharedPreferences("WeddingAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("partner1", partner1);
        editor.putString("partner2", partner2);
        editor.putString("weddingDate", weddingDate);
        editor.apply();
    }

    // Navigate to the dashboard activity
    private void navigateToDashboard() {
        Intent intent = new Intent(profile.this, dashboard.class);
        startActivity(intent);
        finish();
    }

}
