package com.example.groomtime;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.groomtime.models.Service;
import java.util.ArrayList;
import java.util.List;

public class ManageServicesActivity extends AppCompatActivity {
    private RecyclerView servicesRecyclerView;
    private FloatingActionButton addServiceButton;
    private DatabaseReference servicesRef;
    private List<Service> servicesList;
    private ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Services");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        // Initialize views
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        addServiceButton = findViewById(R.id.addServiceButton);

        // Set up RecyclerView
        servicesList = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(servicesList);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Load services
        loadServices();

        // Set up add service button
        addServiceButton.setOnClickListener(v -> {
            // TODO: Show dialog to add new service
            Toast.makeText(this, "Add service functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadServices() {
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                servicesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        servicesList.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageServicesActivity.this,
                    "Error loading services: " + databaseError.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 