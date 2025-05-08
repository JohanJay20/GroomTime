package com.example.groomtime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.groomtime.R;
import com.example.groomtime.adapters.AppointmentAdapter;
import com.example.groomtime.models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> filteredAppointments;
    private String statusFilter;

    public static AppointmentsFragment newInstance(String status) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusFilter = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        // Initialize views
        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data
        filteredAppointments = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(filteredAppointments, false);
        appointmentsRecyclerView.setAdapter(appointmentAdapter);

        return view;
    }

    public void updateAppointments(List<Appointment> appointments) {
        filteredAppointments.clear();
        if (appointments != null) {
            for (Appointment appointment : appointments) {
                if (statusFilter == null || statusFilter.equals(appointment.getStatus())) {
                    filteredAppointments.add(appointment);
                }
            }
        }
        if (appointmentAdapter != null) {
            appointmentAdapter.notifyDataSetChanged();
        }
    }
} 