package com.example.Breast_Cancer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Breast_Cancer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();

        userAdapter = new UserAdapter(userList ,  new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                editUser(user);

            }
            @Override
            public void onDelete(User user) {
                deleteUser(user);

            }
        });

        recyclerView.setAdapter(userAdapter);
        firestore = FirebaseFirestore.getInstance();

        fetchUsersFromFirestore();

    }
    private void fetchUsersFromFirestore() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                                User user = document.toObject(User.class);
                                if (user != null) {
                                    document.getId();
                                    user.setUserId(document.getId());

                                    if (user.getFullName() != null && user.getEmail() != null) {
                                        userList.add(user);
                                    } else {
                                        Log.e("FetchUsers", "Incomplete data for user: " + document.getId());
                                    }
                                }
                            }
                            userAdapter.notifyDataSetChanged();

                        }
                    } else {
                        Log.e("AdminPanelActivity", "Error fetching users: " + task.getException());
                    }
         });
    }
    private void editUser(User user) {

        if (user != null && user.getUserId() != null) {
            Intent intent = new Intent(AdminPanelActivity.this, EditUserActivity.class);
            intent.putExtra("userId", user.getUserId());
            intent.putExtra("fullName", user.getFullName());
            intent.putExtra("email", user.getEmail());
            startActivity(intent);

        } else {
            Toast.makeText(this, "Error: User data is incomplete", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteUser(User user) {

        firestore.collection("users")
                .document(user.getUserId())
                .delete()
                .addOnSuccessListener(aVoid -> {

                    FirebaseAuth.getInstance().getCurrentUser().delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminPanelActivity.this,
                                            "User deleted from Auth and Firestore: " + user.getFullName(),
                                            Toast.LENGTH_SHORT).show();
                                    userList.remove(user);
                                    userAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AdminPanelActivity.this,
                                            "Error deleting user from Auth.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(AdminPanelActivity.this,
                        "Error deleting user from Firestore.",
                        Toast.LENGTH_SHORT).show());
    }

}