package com.example.groomtime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.groomtime.R;
import com.example.groomtime.adapters.AppointmentAdapter;
import com.example.groomtime.models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class ArchiveFragment extends Fragment {
    private RecyclerView archiveRecyclerView;
    private AppointmentAdapter archiveAdapter;
    private List<Appointment> archivedAppointments;
    private FirebaseAuth mAuth;
    private DatabaseReference appointmentsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Initialize views
        archiveRecyclerView = view.findViewById(R.id.archiveRecyclerView);
        archiveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data
        archivedAppointments = new ArrayList<>();
        archiveAdapter = new AppointmentAdapter(archivedAppointments, false);
        archiveRecyclerView.setAdapter(archiveAdapter);

        // Load archived appointments
        loadArchivedAppointments();

        return view;
    }

    private void loadArchivedAppointments() {
        String userId = mAuth.getCurrentUser().getUid();
        Query query = appointmentsRef.orderByChild("userId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                archivedAppointments.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment != null && "archived".equalsIgnoreCase(appointment.getStatus())) {
                        archivedAppointments.add(appointment);
                    }
                }
                archiveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ArchiveFragment", "Error loading archived appointments: " + error.getMessage());
                Toast.makeText(getContext(), 
                    "Error loading archived appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 