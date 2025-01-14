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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TodoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private RecyclerView rvPendingTasks;
    private FloatingActionButton fabAddTask;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        rvPendingTasks = findViewById(R.id.rvPendingTasks);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Set up RecyclerView
        rvPendingTasks.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView();

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
        navigationView.setNavigationItemSelectedListener(item -> handleNavigationItemSelected(item));
    }

    private void setupRecyclerView() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference taskRef = firestore.collection("users").document(userId).collection("tasks");

        // Query to fetch tasks ordered by title
        Query query = taskRef.orderBy("title");

        // Configure FirestoreRecyclerOptions
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();

        // Initialize adapter
        taskAdapter = new TaskAdapter(options, task -> {
            // Delete task from Firestore
            taskRef.document(task.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(TodoActivity.this, "Task Removed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(TodoActivity.this, "Error Removing Task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        rvPendingTasks.setAdapter(taskAdapter);
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
                addTaskToFirestore(title, description);
                dialog.dismiss();
            }
        });
    }

    private void addTaskToFirestore(String title, String description) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference taskRef = firestore.collection("users").document(userId).collection("tasks");

        Task newTask = new Task(title, description);
        taskRef.add(newTask)
                .addOnSuccessListener(documentReference -> {
                    // Set the generated ID to the task
                    newTask.setId(documentReference.getId());
                    // Update Firestore with the ID
                    taskRef.document(newTask.getId()).set(newTask)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to save task ID: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error Adding Task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navHome) {
            startActivity(new Intent(this, dashboard.class));
        } else if (id == R.id.navMyEvents) {
            startActivity(new Intent(this, EventActivity.class));
        } else if (id == R.id.navTasksSchedule) {
            Toast.makeText(this, "Already on To-Do List Page", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navLogout) {
            logout();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(this, signin_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (taskAdapter != null) {
            taskAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (taskAdapter != null) {
            taskAdapter.stopListening();
        }
    }
}
