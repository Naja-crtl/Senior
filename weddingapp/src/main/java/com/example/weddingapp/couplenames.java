package com.example.weddingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class couplenames extends AppCompatActivity {

    private EditText etPartner1, etPartner2;
    private Button btnWeddingDate, btnNext;
    private String selectedWeddingDate = "";

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couplenames);

        firestore = FirebaseFirestore.getInstance();

        etPartner1 = findViewById(R.id.etPartner1);
        etPartner2 = findViewById(R.id.etPartner2);
        btnWeddingDate = findViewById(R.id.btnWeddingDate);
        btnNext = findViewById(R.id.btnNext);

        btnWeddingDate.setOnClickListener(v -> showDatePicker());

        btnNext.setOnClickListener(v -> saveDataAndProceed());
    }

    private void showDatePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Wedding Date");

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            selectedWeddingDate = sdf.format(selection);
            btnWeddingDate.setText("Wedding Date: " + selectedWeddingDate);
        });

        datePicker.show(getSupportFragmentManager(), "WEDDING_DATE_PICKER");
    }

    private void saveDataAndProceed() {
        String partner1 = etPartner1.getText().toString();
        String partner2 = etPartner2.getText().toString();

        if (partner1.isEmpty() || partner2.isEmpty() || selectedWeddingDate.isEmpty()) {
            Toast.makeText(couplenames.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                Map<String, Object> coupleData = new HashMap<>();
                coupleData.put("partner1", partner1);
                coupleData.put("partner2", partner2);
                coupleData.put("weddingDate", selectedWeddingDate);

                firestore.collection("users").document(userId).collection("couples")
                        .add(coupleData) // Add new couple data under the `couples` subcollection
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(couplenames.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate to Dashboard
                            Intent intent = new Intent(couplenames.this, dashboard.class);
                            intent.putExtra("partner1", partner1);
                            intent.putExtra("partner2", partner2);
                            intent.putExtra("weddingDate", selectedWeddingDate);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(couplenames.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }
}
