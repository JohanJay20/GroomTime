package com.example.groomtime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.groomtime.models.Service;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> servicesList;

    public ServiceAdapter(List<Service> servicesList) {
        this.servicesList = servicesList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = servicesList.get(position);
        holder.nameText.setText(service.getName());
        holder.descriptionText.setText(service.getDescription());
        holder.priceText.setText(String.format(Locale.getDefault(), "₱%.2f", service.getPrice()));
        holder.durationText.setText(String.format("%d minutes", service.getDuration()));
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView descriptionText;
        TextView priceText;
        TextView durationText;

        ServiceViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.serviceNameText);
            descriptionText = itemView.findViewById(R.id.serviceDescriptionText);
            priceText = itemView.findViewById(R.id.servicePriceText);
            durationText = itemView.findViewById(R.id.serviceDurationText);
        }
    }
} 