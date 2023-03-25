package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class VehiclesInfoActivity extends AppCompatActivity{

    RecyclerViewAdapter adapter;
    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;

    private User user;

    TextView noVehiclesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_vehicles_info);

        user = getIntent().getParcelableExtra("user");

        recyclerView = findViewById(R.id.vehicleRecyclerView);

        noVehiclesTextView = findViewById(R.id.noVehiclesTextView);
        noVehiclesTextView.setVisibility(View.INVISIBLE);
        firestore=FirebaseFirestore.getInstance();
        readData();
        super.onCreate(savedInstanceState);

    }

    public void readData() {
        firestore.collection("vehicles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> values = queryDocumentSnapshots.getDocuments();
                ArrayList<Vehicle> vehicles = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot : values){
                    vehicles.add(documentSnapshot.toObject(Vehicle.class));
                }

                if(vehicles.size()==0){
                    noVehiclesTextView.setVisibility(View.VISIBLE);
                }

                adapter = new RecyclerViewAdapter(VehiclesInfoActivity.this, vehicles, new RecyclerViewInterface() {
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

    public void goToAddVehicle(View v){
        Intent intent = new Intent(getBaseContext(), AddVehicleActivity.class);

        intent.putExtra("user",user);
        startActivity(intent);
    }

    public void goToProfile(View v){
        Intent intent = new Intent(getBaseContext(), UserProfileActivity.class);

        intent.putExtra("user",user);
        startActivity(intent);
    }

}