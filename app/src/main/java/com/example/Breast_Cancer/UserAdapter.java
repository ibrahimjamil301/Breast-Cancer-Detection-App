package com.example.Breast_Cancer;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> userList;
    private final OnUserActionListener listener;
    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }
    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout itemLayout = new LinearLayout(parent.getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(16, 16, 16, 16);

        TextView nameTextView = new TextView(parent.getContext());
        nameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(24);
        itemLayout.addView(nameTextView);

        TextView emailTextView = new TextView(parent.getContext());
        emailTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        emailTextView.setTextColor(Color.BLACK);
        emailTextView.setTextSize(18);
        itemLayout.addView(emailTextView);

        LinearLayout buttonsLayout = new LinearLayout(parent.getContext());
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonsLayout.setWeightSum(2);

        Button editButton = new Button(parent.getContext());
        editButton.setText("Edit");
        editButton.setBackgroundColor(Color.parseColor("#736D66"));
        editButton.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        editButton.setLayoutParams(editParams);

        Button deleteButton = new Button(parent.getContext());
        deleteButton.setText("Delete");
        deleteButton.setBackgroundColor(Color.parseColor("#700F1A"));
        deleteButton.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        deleteButton.setLayoutParams(deleteParams);

        buttonsLayout.addView(editButton);
        buttonsLayout.addView(deleteButton);
        itemLayout.addView(buttonsLayout);

        return new UserViewHolder(itemLayout, nameTextView, emailTextView, editButton, deleteButton);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getFullName());
        holder.emailTextView.setText(user.getEmail());

        holder.editButton.setOnClickListener(v -> listener.onEdit(user));
        holder.deleteButton.setOnClickListener(v -> {
            listener.onDelete(user);
            Toast.makeText(v.getContext(), "Deleted: " + user.getFullName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;
        Button editButton, deleteButton;
        public UserViewHolder(@NonNull View itemView, TextView nameTextView, TextView emailTextView, Button editButton, Button deleteButton) {
            super(itemView);
            this.nameTextView = nameTextView;
            this.emailTextView = emailTextView;
            this.editButton = editButton;
            this.deleteButton = deleteButton;
        }
    }
}


