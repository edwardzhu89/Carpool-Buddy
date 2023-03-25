package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class AddVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseFirestore firestore;

    private Spinner vehicleSpinner;
    private String type;
    private int param;

    private EditText modelEditText;
    private EditText basePriceEditText;
    private EditText capacityEditText;
    private EditText paramEditText;

    private String userEmail;
    private String userName;

    private Button selectButton;

    private User user;

    private ImageView profileImageView;

    private Uri mImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private boolean chosen =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        user = getIntent().getParcelableExtra("user");
        userName = user.getName();
        userEmail = user.getEmail();

        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        firestore = FirebaseFirestore.getInstance();

        vehicleSpinner = findViewById(R.id.vehicleSpinner);

        profileImageView = findViewById(R.id.uploadImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.vehicle, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(adapter);
        vehicleSpinner.setOnItemSelectedListener(this);

        selectButton = findViewById(R.id.selectButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        modelEditText = findViewById(R.id.modelEditText);
        basePriceEditText = findViewById(R.id.basePriceEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        paramEditText = findViewById(R.id.paramEditText);
    }

    public void uploadData(View view) {

        String model;
        int capacity;
        int basePrice;

        if(formValid()){
            model = modelEditText.getText().toString();
            capacity = Integer.parseInt(capacityEditText.getText().toString());
            basePrice = Integer.parseInt(basePriceEditText.getText().toString());
            param = Integer.parseInt(paramEditText.getText().toString());
        }
        else{
            Toast.makeText(AddVehicleActivity.this, "Missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri != null) {
            File file = new File(String.valueOf(mImageUri));
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images");

            storageRef.child(file.getName()).putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddVehicleActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            Task<Uri> downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String generatedFilePath = downloadUri.getResult().toString();
                                    uploadData(model, capacity, basePrice, param, generatedFilePath);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddVehicleActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            uploadData(model, capacity, basePrice, param, "https://firebasestorage.googleapis.com/v0/b/carpool-buddy-9333f.appspot.com/o/images%2FNo_image_available.svg.png?alt=media&token=a39b3735-c89e-4457-ae58-475274456fa2");
        }
    }

    private void openFileChooser(){

        Intent intent = new Intent();
        profileImageView.setBackgroundTintList(null);
        profileImageView.setColorFilter(null);
        profileImageView.clearColorFilter();

        ImageViewCompat.setImageTintList(profileImageView, null);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        chosen = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null){
            mImageUri=data.getData();
            Picasso.get()
                    .load(mImageUri)
                    .into(profileImageView);

            profileImageView.setImageURI(mImageUri);
        }
    }

    public void clear(View v){
        modelEditText.getText().clear();
        basePriceEditText.getText().clear();
        paramEditText.getText().clear();
        capacityEditText.getText().clear();

        type= "";
        param=0;

        mImageUri=null;
        profileImageView.setColorFilter(android.R.color.white);
        profileImageView.setImageResource(R.drawable.ic_baseline_directions_car_24);
    }

    public void uploadData(String model, int capacity, int basePrice, int param, String path){
        switch (type) {
            case "Car":
                Car newCar = new Car(param);
                newCar.setOwner(userName);
                newCar.setModel(model);
                newCar.setCapacity(capacity);
                newCar.setBasePrice(basePrice);
                String id = String.valueOf(UUID.randomUUID());
                newCar.setVehicleID(id);
                newCar.setRange(param);
                newCar.setVehicleType("Car");
                newCar.setOpen(true);
                newCar.setOwnerEmail(userEmail);
                newCar.setRidersNames(new ArrayList<String>());
                newCar.setRidersUIDs(new ArrayList<String>());

                newCar.setLink(path);

                newCar.setRidersLat(new ArrayList<String>());
                newCar.setRidersLong(new ArrayList<String>());

                firestore.collection("vehicles").document(newCar.getVehicleID()).set(newCar);
                firestore.collection("users").document(userEmail).collection("vehicles").document(newCar.getVehicleID()).set(newCar).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddVehicleActivity.this, "Added vehicle "+newCar.getModel(), Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(this, VehiclesInfoActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);

                break;

            case "Van":
                Van newVan = new Van(param);
                newVan.setOwner(userName);
                newVan.setOwnerEmail(userEmail);
                newVan.setModel(model);
                newVan.setCapacity(capacity);
                newVan.setBasePrice(basePrice);
                newVan.setRange(param);
                newVan.setVehicleID(String.valueOf(UUID.randomUUID()));
                newVan.setVehicleType("Van");
                newVan.setOpen(true);
                newVan.setRidersNames(new ArrayList<String>());
                newVan.setRidersUIDs(new ArrayList<String>());

                newVan.setLink(path);

                newVan.setRidersLong(new ArrayList<String>());
                newVan.setRidersLat(new ArrayList<String>());

                firestore.collection("vehicles").document(newVan.getVehicleID()).set(newVan);
                firestore.collection("users").document(userEmail).collection("vehicles").document(newVan.getVehicleID()).set(newVan).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddVehicleActivity.this, "Added vehicle "+newVan.getModel(), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent2 = new Intent(this, VehiclesInfoActivity.class);
                intent2.putExtra("user",user);
                startActivity(intent2);

                break;
        }
    }

    public boolean formValid(){
        return modelEditText.getText().toString().trim().length() != 0
                && basePriceEditText.getText().toString().trim().length() != 0
                && capacityEditText.getText().toString().trim().length() != 0
                && paramEditText.getText().toString().trim().length() != 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        type=text;
        if(text.equals("Car")){
            paramEditText.setHint("Range");
        }
        else if(text.equals("Van")){
            paramEditText.setHint("Range");
        }
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}