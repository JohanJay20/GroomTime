package com.example.groomtime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.groomtime.models.User;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> usersList;

    public UserAdapter(List<User> usersList) {
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.nameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());
        holder.roleText.setText(user.getRole());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView emailText;
        TextView roleText;

        UserViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.userNameText);
            emailText = itemView.findViewById(R.id.userEmailText);
            roleText = itemView.findViewById(R.id.userRoleText);
        }
    }
} 