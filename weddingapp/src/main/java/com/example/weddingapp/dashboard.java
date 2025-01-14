package com.example.weddingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

    private LinearLayout llMyEvents, llBudget, llTodoList, llCategories, llGuestList, llTimeline;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

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

        llMyEvents = findViewById(R.id.llMyEvents);
        llBudget = findViewById(R.id.llBudget);
        llTodoList = findViewById(R.id.llTodoList);
        llCategories = findViewById(R.id.llCategories);
        llGuestList = findViewById(R.id.llGuestList);
        llTimeline = findViewById(R.id.llTimeline);

        // Set up navigation menu
        setupNavigationMenu();

        // Load data
        loadUserData();

        // Set up button navigation
        setupButtonNavigation();
    }

    private void setupNavigationMenu() {
        // Menu Button Click Listener
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Navigation View Item Click Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navMyEvents) {
                startActivity(new Intent(this, EventActivity.class));
            } else if (id == R.id.navTasksSchedule) {
                startActivity(new Intent(this, TodoActivity.class));
            } else if (id == R.id.navLogout) {
                logout();
            }
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }

    private void loadUserData() {
        // Check if data is already in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("WeddingAppPrefs", MODE_PRIVATE);
        String partner1 = sharedPreferences.getString("partner1", null);
        String partner2 = sharedPreferences.getString("partner2", null);
        String weddingDate = sharedPreferences.getString("weddingDate", null);

        if (partner1 != null && partner2 != null && weddingDate != null) {
            // Use cached data
            tvWelcomeMessage.setText("Welcome " + partner1 + " and " + partner2 + "!");
            calculateCountdown(weddingDate);
        } else {
            // Fetch data from Firestore
            fetchUserDataFromFirestore();
        }
    }

    private void fetchUserDataFromFirestore() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("couples")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Assume only one document exists
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String partner1 = doc.getString("partner1");
                        String partner2 = doc.getString("partner2");
                        String weddingDate = doc.getString("weddingDate");

                        // Update UI
                        tvWelcomeMessage.setText("Welcome " + partner1 + " and " + partner2 + "!");
                        calculateCountdown(weddingDate);

                        // Save to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("WeddingAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("partner1", partner1);
                        editor.putString("partner2", partner2);
                        editor.putString("weddingDate", weddingDate);
                        editor.apply();
                    } else {
                        Toast.makeText(this, "No data found for the user!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

    private void setupButtonNavigation() {
        llMyEvents.setOnClickListener(v -> startActivity(new Intent(this, EventActivity.class)));
        llBudget.setOnClickListener(v -> Toast.makeText(this, "Budget Activity Not Implemented Yet", Toast.LENGTH_SHORT).show());
        llTodoList.setOnClickListener(v -> startActivity(new Intent(this, TodoActivity.class)));
        llCategories.setOnClickListener(v -> Toast.makeText(this, "Categories Activity Not Implemented Yet", Toast.LENGTH_SHORT).show());
        llGuestList.setOnClickListener(v -> Toast.makeText(this, "Guest List Activity Not Implemented Yet", Toast.LENGTH_SHORT).show());
        llTimeline.setOnClickListener(v -> Toast.makeText(this, "Timeline Activity Not Implemented Yet", Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("WeddingAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Logout user
        firebaseAuth.signOut();

        // Navigate to sign-in page
        Intent intent = new Intent(this, signin_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
