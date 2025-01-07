package com.example.weddingapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class dashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvWelcomeMessage, tvCountdown;
    private ImageButton btnMenu;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Retrieve data from Firestore
        firestore.collection("couples")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.forEach(document -> {
                            String partner1 = document.getString("partner1");
                            String partner2 = document.getString("partner2");
                            String weddingDate = document.getString("weddingDate");

                            tvWelcomeMessage.setText("Welcome " + partner1 + " and " + partner2 + "!");
                            calculateCountdown(weddingDate);
                        });
                    } else {
                        tvWelcomeMessage.setText("No data available.");
                        tvCountdown.setText("Set your wedding date.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(dashboard.this, "Failed to retrieve data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateCountdown(String weddingDate) {
        if (weddingDate != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date wedding = dateFormat.parse(weddingDate);
                Date today = new Date();

                long diffInMillis = wedding.getTime() - today.getTime();
                long daysRemaining = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                if (daysRemaining >= 0) {
                    tvCountdown.setText("Days until the wedding: " + daysRemaining + " days");
                } else {
                    tvCountdown.setText("The wedding date has passed!");
                }
            } catch (ParseException e) {
                tvCountdown.setText("Invalid wedding date!");
            }
        } else {
            tvCountdown.setText("Wedding date not set.");
        }
    }
}
