package com.example.carpoolbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private User user;

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView userTypeTextView;
    private TextView paramTextView;
    private Button logOutButton;

    private FirebaseFirestore firestore;

    RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firestore=FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.ownVehicleRecyclerView);

        user = getIntent().getParcelableExtra("user");

        readData();

        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        paramTextView = findViewById(R.id.paramTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);
        logOutButton = findViewById(R.id.logOutButton);

        nameTextView.setText("Name: "+user.getName());
        emailTextView.setText("Email: "+user.getEmail());
        userTypeTextView.setText("User Type: "+user.getUserType());
        if(user.getUserType().equals("Teacher")){
            paramTextView.setText("In School Role: " + user.getParam());
        }
        if(user.getUserType().equals("Student") || user.getUserType().equals("Alumni")){
            paramTextView.setText("Graduation Year: " + user.getParam());
        }
    }

    public void returnHome(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getBaseContext(), AuthActivity.class);
        startActivity(intent);
    }

    public void readData() {
        firestore.collection("users").document(user.getEmail()).collection("vehicles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> values = queryDocumentSnapshots.getDocuments();
                ArrayList<Vehicle> vehicles = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot : values){
                    vehicles.add(documentSnapshot.toObject(Vehicle.class));
                }
                adapter = new RecyclerViewAdapter(UserProfileActivity.this, vehicles, new RecyclerViewInterface() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getBaseContext(), VehicleProfileActivity.class);
                        intent.putExtra("vehicle",vehicles.get(position));
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(int position) {
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                recyclerView.setAdapter(adapter);
            }
        });
    }
}