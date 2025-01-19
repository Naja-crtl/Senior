package com.example.weddingapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView tvPageTitle;
    private RecyclerView rvEventList;
    private LinearLayout eventInputContainer;
    private Button btnAddEvent, btnSaveEvent, btnChooseDate, btnChooseTime;
    private EditText etEventName, etEventNotes;

    private String selectedDate = "";
    private String selectedTime = "";

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private FirestoreRecyclerAdapter<Event, EventViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        tvPageTitle = findViewById(R.id.tvPageTitle);
        rvEventList = findViewById(R.id.rvEventList);
        eventInputContainer = findViewById(R.id.eventInputContainer);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        etEventName = findViewById(R.id.etEventName);
        etEventNotes = findViewById(R.id.etEventNotes);

        // Set up RecyclerView
        rvEventList.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView();

        // Menu Button Click Listener
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);

        // Add Event Button Listener
        btnAddEvent.setOnClickListener(v -> {
            eventInputContainer.setVisibility(View.VISIBLE);
            btnAddEvent.setVisibility(View.GONE);
        });

        // Choose Date Button Listener
        btnChooseDate.setOnClickListener(v -> openDatePicker());

        // Choose Time Button Listener
        btnChooseTime.setOnClickListener(v -> openTimePicker());

        // Save Event Button Listener
        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void setupRecyclerView() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference eventRef = firestore.collection("users").document(userId).collection("events");

        Query query = eventRef.orderBy("date");

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event event) {
                holder.tvEventName.setText(event.getName());
                holder.tvEventDate.setText("Date: " + event.getDate());
                holder.tvEventTime.setText("Time: " + event.getTime());
                holder.tvEventNotes.setText("Notes: " + event.getNotes());

                holder.btnDelete.setOnClickListener(v -> {
                    eventRef.document(event.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(EventActivity.this, "Event Deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(EventActivity.this, "Error Deleting Event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });
            }

            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
                return new EventViewHolder(view);
            }
        };

        rvEventList.setAdapter(adapter);
    }

    private boolean handleNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navHome) {
            startActivity(new Intent(this, dashboard.class));
        } else if (id == R.id.navMyEvents) {
            Toast.makeText(this, "Already on Events Page", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navTasksSchedule) {
            startActivity(new Intent(this, TodoActivity.class));
        } else if (id == R.id.navLogout) {
            logout();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            btnChooseDate.setText("Date: " + selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            btnChooseTime.setText("Time: " + selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String notes = etEventNotes.getText().toString().trim();

        if (name.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();
        CollectionReference eventRef = firestore.collection("users").document(userId).collection("events");

        Event newEvent = new Event(name, selectedDate, selectedTime, notes);
        eventRef.add(newEvent)
                .addOnSuccessListener(documentReference -> {
                    newEvent.setId(documentReference.getId());
                    eventRef.document(newEvent.getId()).set(newEvent)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Event Added", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to Save Event ID: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    clearInputFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error Adding Event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearInputFields() {
        etEventName.setText("");
        etEventNotes.setText("");
        btnChooseDate.setText("Choose Event Date");
        btnChooseTime.setText("Choose Event Time");
        selectedDate = "";
        selectedTime = "";
        eventInputContainer.setVisibility(View.GONE);
        btnAddEvent.setVisibility(View.VISIBLE);
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
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvEventTime, tvEventNotes;
        ImageButton btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventNotes = itemView.findViewById(R.id.tvEventNotes);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
