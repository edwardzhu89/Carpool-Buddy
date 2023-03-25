package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VehicleProfileActivity extends AppCompatActivity {

    private String model;
    private String owner;
    private List<String> ridersUuid;
    private List<String> ridersNames;

    private List<String> ridersLong;
    private List<String> ridersLat;

    private int capacity;
    private double price;
    private String uuid;
    private String ownerEmail;

    private Button reserveButton;

    private FirebaseFirestore firestore;

    private User user;
    private Vehicle vehicle;

    private ImageButton mapButton;

    private TextView modelTextView;
    private TextView capacityTextView;
    private TextView ownerTextView;
    private TextView priceTextView;

    private ImageView profileImageView;

    private RecyclerView seats;
    private SeatsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        reserveButton = findViewById(R.id.reserveButton);
        Button removeButton = findViewById(R.id.removeButton);

        firestore=FirebaseFirestore.getInstance();

        mapButton = findViewById(R.id.mapButton);

        seats = findViewById(R.id.seatsRecyclerView);

        profileImageView = findViewById(R.id.profileImageView);

        vehicle = getIntent().getParcelableExtra("vehicle");
        user = getIntent().getParcelableExtra("user");

        System.out.println(user.toString());

        ridersUuid = vehicle.getRidersUIDs();
        ridersNames = vehicle.getRidersNames();

        model = vehicle.getModel();
        owner = vehicle.getOwner();
        uuid = vehicle.getVehicleID();
        ownerEmail=vehicle.getOwnerEmail();
        capacity = vehicle.getCapacity();
        price = vehicle.getBasePrice();

        ridersLong = vehicle.getRidersLong();
        ridersLat = vehicle.getRidersLat();

        if(!vehicle.isOpen() && !ownerEmail.equals(user.getEmail())){
            reserveButton.setEnabled(false);
            reserveButton.setText("Ride is closed");
        }

        if(ownerEmail.equals(user.getEmail())){
            removeButton.setText("Remove Vehicle");
            if(vehicle.isOpen()) reserveButton.setText("Close vehicle");
            else{reserveButton.setText("Open vehicle");}
        }

        Picasso.get().load(vehicle.getLink()).resize(300,200).centerInside().into(profileImageView);
        adapter = new SeatsRecyclerViewAdapter(VehicleProfileActivity.this, ridersNames, new RecyclerViewInterface() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemLongClick(int position) {

            }
        });

        if(capacity==0){
            reserveButton.setEnabled(false);
        }

        if(!user.getEmail().equals(ownerEmail) && !ridersNames.contains(user.getName()+",  "+user.getUserType()+",  "+user.getEmail())){
            removeButton.setEnabled(false);
        }

        modelTextView = findViewById(R.id.modelTextView);
        capacityTextView = findViewById(R.id.capacityTextView);
        ownerTextView = findViewById(R.id.ownerTextView);
        priceTextView = findViewById(R.id.priceTextView);

        if(!Objects.equals(user.getEmail(), ownerEmail)){
            mapButton.setVisibility(View.INVISIBLE);
        }

        modelTextView.setText("Model: "+model);
        capacityTextView.setText("Capacity: "+capacity);
        ownerTextView.setText("Owner: "+owner);
        priceTextView.setText("Price: "+price);

        seats.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        seats.setAdapter(adapter);

    }

    public void goToMap(View view){

        Intent intent = new Intent(getBaseContext(), MapActivity2.class);

        intent.putExtra("ridersLat", (ArrayList<String>) ridersLat);
        intent.putExtra("ridersLong", (ArrayList<String>) ridersLong);
        intent.putExtra("ridersNames", (ArrayList<String>) ridersNames);

        intent.putExtra("user",user);

        startActivity(intent);

    }

    public void orderRide(View view){

        if(ownerEmail.equals(user.getEmail())){
            vehicle.setOpen(!vehicle.isOpen());
        }

        else {

            ridersLong.add(Double.toString(user.getLongitude()));
            ridersLat.add(Double.toString(user.getLat()));

            ridersUuid.add(user.getEmail());
            ridersNames.add(user.getName() + ",  " + user.getUserType() + ",  " + user.getEmail());

            vehicle.setRidersUIDs((ArrayList<String>) ridersUuid);
            vehicle.setRidersNames((ArrayList<String>) ridersNames);

            vehicle.setRidersLong((ArrayList<String>) ridersLong);
            vehicle.setRidersLat((ArrayList<String>) ridersLat);

            vehicle.setCapacity(vehicle.getCapacity() - 1);
        }

        firestore.collection("vehicles").document(uuid).set(vehicle);
        firestore.collection("users").document(ownerEmail).collection("vehicles").document(uuid).set(vehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(ownerEmail.equals(user.getEmail())){
                    Toast.makeText(VehicleProfileActivity.this, "Changed settings for " + vehicle.getModel(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(VehicleProfileActivity.this, "Reserved seat for " + vehicle.getModel(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent = new Intent(getBaseContext(), VehiclesInfoActivity.class);

        intent.putExtra("user",user);

        startActivity(intent);
    }

    public void removeReservation(View view){

        if (!Objects.equals(ownerEmail, user.getEmail())) {

            ridersUuid.remove(user.getEmail());
            ridersNames.remove(user.getName() + ",  " + user.getUserType() + ",  " + user.getEmail());

            ridersLong.remove(Double.toString(user.getLongitude()));
            ridersLat.remove(Double.toString(user.getLat()));

            vehicle.setRidersUIDs((ArrayList<String>) ridersUuid);
            vehicle.setRidersNames((ArrayList<String>) ridersNames);

            vehicle.setRidersLong((ArrayList<String>) ridersLong);
            vehicle.setRidersLat((ArrayList<String>) ridersLat);

            vehicle.setCapacity(vehicle.getCapacity() + 1);

            firestore.collection("vehicles").document(uuid).set(vehicle);
            firestore.collection("users").document(ownerEmail).collection("vehicles").document(uuid).set(vehicle).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(VehicleProfileActivity.this, "Reservation removed for " + vehicle.getModel(), Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(getBaseContext(), VehiclesInfoActivity.class);

            intent.putExtra("user", user);

            startActivity(intent);
        }

        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Remove Vehicle "+model+"?");
            builder.setMessage("Confirm immediately");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firestore.collection("vehicles").document(uuid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VehicleProfileActivity.this, "Error when removing", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });

                            firestore.collection("users").document(ownerEmail).collection("vehicles").document(uuid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(VehicleProfileActivity.this, "Vehicle "+vehicle.getModel() + " removed", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(VehicleProfileActivity.this, "Error when removing", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });

                            Intent intent = new Intent(getBaseContext(), VehiclesInfoActivity.class);

                            intent.putExtra("user", user);

                            startActivity(intent);

                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}