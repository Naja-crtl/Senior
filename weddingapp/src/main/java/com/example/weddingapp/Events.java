package com.example.weddingapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Events extends AppCompatActivity {

    private RecyclerView rvEventList;
    private LinearLayout eventInputContainer;
    private Button btnAddEvent, btnChooseDate, btnChooseTime, btnSaveEvent;
    private EditText etEventName, etEventNotes;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton btnMenu;
    private TextView tvPageTitle;

    private String selectedDate = "";
    private String selectedTime = "";
    private ArrayList<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Initialize Views
        rvEventList = findViewById(R.id.rvEventList);
        eventInputContainer = findViewById(R.id.eventInputContainer);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        etEventName = findViewById(R.id.etEventName);
        etEventNotes = findViewById(R.id.etEventNotes);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
        tvPageTitle = findViewById(R.id.tvPageTitle);

        // Set Page Title
        tvPageTitle.setText("My Events");

        // Set up the menu button to open the drawer
        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView); // Open Drawer
            } else {
                drawerLayout.closeDrawer(navigationView); // Close Drawer
            }
        });

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navHome) {
                Intent intent = new Intent(Events.this, dashboard.class);
                startActivity(intent);
            } else if (id == R.id.navMyEvents) {
                Toast.makeText(Events.this, "Already on Events Page", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navLogout) {
                Toast.makeText(Events.this, "Logout selected", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.navTasksSchedule) {
            Intent intent = new Intent(Events.this,TodoActivity.class);
            startActivity(intent);
            }
            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        // Setup RecyclerView
        eventAdapter = new EventAdapter(eventList, position -> {
            eventList.remove(position);
            eventAdapter.notifyItemRemoved(position);
            Toast.makeText(Events.this, "Event deleted", Toast.LENGTH_SHORT).show();
        });

        rvEventList.setLayoutManager(new LinearLayoutManager(this));
        rvEventList.setAdapter(eventAdapter);

        // Handle Add Event Button
        btnAddEvent.setOnClickListener(v -> {
            eventInputContainer.setVisibility(View.VISIBLE);
            btnAddEvent.setVisibility(View.GONE);
        });

        // Handle Date Picker
        btnChooseDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select Event Date");
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            builder.setCalendarConstraints(constraintsBuilder.build());
            MaterialDatePicker<Long> datePicker = builder.build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDate = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selection);
                btnChooseDate.setText("Selected Date: " + selectedDate);
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // Handle Time Picker
        btnChooseTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(Events.this, (view, hourOfDay, minuteOfHour) -> {
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                btnChooseTime.setText("Selected Time: " + selectedTime);
            }, hour, minute, true);

            timePicker.show();
        });

        // Handle Save Event Button
        btnSaveEvent.setOnClickListener(v -> {
            String eventName = etEventName.getText().toString();
            String eventNotes = etEventNotes.getText().toString();

            if (eventName.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty() || eventNotes.isEmpty()) {
                Toast.makeText(Events.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                Event event = new Event(eventName, selectedDate, selectedTime, eventNotes);
                eventList.add(event);

                eventAdapter.notifyItemInserted(eventList.size() - 1);

                eventInputContainer.setVisibility(View.GONE);
                btnAddEvent.setVisibility(View.VISIBLE);

                etEventName.setText("");
                etEventNotes.setText("");
                btnChooseDate.setText("Choose Event Date");
                btnChooseTime.setText("Choose Event Time");
                selectedDate = "";
                selectedTime = "";
            }
        });
    }
}
