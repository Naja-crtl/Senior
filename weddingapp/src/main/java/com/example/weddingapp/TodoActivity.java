package com.example.weddingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private RecyclerView rvPendingTasks;
    private FloatingActionButton fabAddTask;
    private ArrayList<Task> pendingTasks;
    private TaskAdapter pendingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        rvPendingTasks = findViewById(R.id.rvPendingTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Initialize task list and adapter
        pendingTasks = new ArrayList<>();
        pendingAdapter = new TaskAdapter(pendingTasks, position -> {
            // Remove task from the list
            pendingTasks.remove(position);
            pendingAdapter.notifyItemRemoved(position);
            Toast.makeText(this, "Task Removed", Toast.LENGTH_SHORT).show();
        });

        // Set up RecyclerView
        rvPendingTasks.setLayoutManager(new LinearLayoutManager(this));
        rvPendingTasks.setAdapter(pendingAdapter);

        // Handle Menu Button
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Handle Floating Action Button (Add Task)
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navHome) {
                    Intent intent = new Intent(TodoActivity.this, dashboard.class);
                    startActivity(intent);
                } else if (id == R.id.navMyEvents) {
                    Intent intent = new Intent(TodoActivity.this, Events.class);
                    startActivity(intent);
                }
                else if (id == R.id.navTasksSchedule) {
                Toast.makeText(TodoActivity.this, "Already on To-Do List Page", Toast.LENGTH_SHORT).show();}

                else if (id == R.id.navLogout) {
                    Toast.makeText(TodoActivity.this, "Logout Selected", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    private void showAddTaskDialog() {
        // Inflate the custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null, false);
        builder.setView(dialogView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize views in the dialog
        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        Button btnAddTask = dialogView.findViewById(R.id.btnAddTask);

        // Handle Add Task button click
        btnAddTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String description = etTaskDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add the task to the list and notify the adapter
                Task newTask = new Task(title, description);
                pendingTasks.add(newTask);
                pendingAdapter.notifyItemInserted(pendingTasks.size() - 1);
                dialog.dismiss();
                Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
