package com.example.weddingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnMenu = findViewById(R.id.btnMenu);

        // Menu button toggle
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Set Navigation Menu Item Click Listener
        navigationView.setNavigationItemSelectedListener(this::handleMenuItemClick);

        // Retrieve user-specific data from Firestore
        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference couplesRef = firestore.collection("users").document(userId).collection("couples");

        couplesRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assume only one document exists in the `couples` subcollection for simplicity
                        DocumentSnapshot coupleDoc = querySnapshot.getDocuments().get(0);

                        String partner1 = coupleDoc.getString("partner1");
                        String partner2 = coupleDoc.getString("partner2");
                        String weddingDate = coupleDoc.getString("weddingDate");

                        tvWelcomeMessage.setText("Welcome " + partner1 + " and " + partner2 + "!");
                        calculateCountdown(weddingDate);
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

    private boolean handleMenuItemClick(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navLogout) {
            logout();
        } else if (id == R.id.navMyEvents) {
            Intent intent = new Intent(dashboard.this, Events.class);
            startActivity(intent);
        } else if (id == R.id.navTasksSchedule) {
            Intent intent = new Intent(dashboard.this, TodoActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void logout() {
        // Log out from Firebase
        firebaseAuth.signOut();

        // Clear any session-related shared preferences if applicable
        getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

        // Navigate to the sign-in page
        Intent intent = new Intent(dashboard.this, signin_page.class);
        startActivity(intent);
        finish();

        Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
    }
}
