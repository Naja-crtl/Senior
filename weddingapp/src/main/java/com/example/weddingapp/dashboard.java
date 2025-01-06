package com.example.weddingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnMenu = findViewById(R.id.btnMenu);

        // Set click listener on the Menu Button to open the drawer
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Set up navigation menu item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navMyEvents) {
                Intent intent = new Intent(dashboard.this, Events.class);
                startActivity(intent);
            } else if (id == R.id.navTasksSchedule) {
                Intent intent = new Intent(dashboard.this,TodoActivity.class);
                startActivity(intent);
            } else if (id == R.id.navLogout) {
                Toast.makeText(dashboard.this, "Logout selected", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        // Retrieve Intent data
        String partner1 = getIntent().getStringExtra("partner1");
        String partner2 = getIntent().getStringExtra("partner2");
        String weddingDate = getIntent().getStringExtra("weddingDate");

        if (partner1 == null) partner1 = "Partner 1";
        if (partner2 == null) partner2 = "Partner 2";

        tvWelcomeMessage.setText("Welcome " + partner1 + " and " + partner2 + "!");

        // Calculate wedding countdown
        calculateCountdown(weddingDate);

        // Handle "My Events" navigation
        LinearLayout llMyEvents = findViewById(R.id.llMyEvents);
        llMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, Events.class);
            startActivity(intent);
        });

        // Handle "To-Do List" navigation
        LinearLayout llTodoList = findViewById(R.id.llTodoList);
        llTodoList.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, TodoActivity.class);
            startActivity(intent);
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
