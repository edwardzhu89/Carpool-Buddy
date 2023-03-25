package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String emailString;
    private String passwordString;
    private String name;
    private String param;
    private String type;

    private Button nextButton;

    private double longitude;
    private double lat;

    private AutoCompleteTextView mSearchText;
    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mAuth = FirebaseAuth.getInstance();

        nextButton = findViewById(R.id.nextButton);

        Bundle b = getIntent().getExtras();
        emailString = b.getString("emailString");
        passwordString = b.getString("passwordString");
        name = b.getString("name");
        param = b.getString("param");
        type = b.getString("type");

        nextButton.setEnabled(false);
        mSearchText = findViewById(R.id.input_search);

        getLocationPermission();
    }

    private void init(){

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    public void signUp(View view){
        uploadData(emailString,name);
        mAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user,emailString);
                }else {
                    Toast.makeText(MapActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateUI(FirebaseUser currentUser, String email){
        Intent intent = new Intent(this, VehiclesInfoActivity.class);
        Log.d("open",email);
        if(currentUser != null){
            DocumentReference docRef = firestore.collection("users").document(email);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            intent.putExtra("user",user);
                            Toast.makeText(MapActivity.this, "Welcome "+user.getName(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    public void uploadData(String emailString, String name){
        switch (type) {
            case "Student":
                Student newStudent = new Student();
                newStudent.setEmail(emailString);
                newStudent.setUserType("Student");
                newStudent.setName(name);
                newStudent.setUid(String.valueOf(UUID.randomUUID()));
                newStudent.setLongitude(longitude);
                newStudent.setLat(lat);
                newStudent.setParam(param);
                Log.d("make new user", newStudent.toString());
                firestore.collection("users").document(emailString).set(newStudent);
                break;
            case "Teacher":
                Teacher newTeacher = new Teacher();
                newTeacher.setEmail(emailString);
                newTeacher.setUid(String.valueOf(UUID.randomUUID()));
                newTeacher.setUserType("Teacher");
                newTeacher.setName(name);
                newTeacher.setLongitude(longitude);
                newTeacher.setLat(lat);
                newTeacher.setParam(param);
                Log.d("make new user", newTeacher.toString());
                firestore.collection("users").document(emailString).set(newTeacher);
                break;
            case "Parent":
                Parent newParent = new Parent();
                newParent.setEmail(emailString);
                newParent.setUserType("Parent");
                newParent.setUid(String.valueOf(UUID.randomUUID()));
                newParent.setName(name);
                newParent.setLongitude(longitude);
                newParent.setLat(lat);
                Log.d("make new user", newParent.toString());
                firestore.collection("users").document(emailString).set(newParent);
                break;
            default:
                Alumni newAlumni = new Alumni();
                newAlumni.setEmail(emailString);
                newAlumni.setUserType("Alumni");
                newAlumni.setParam(param);
                newAlumni.setUid(String.valueOf(UUID.randomUUID()));
                newAlumni.setName(name);
                Log.d("make new user", newAlumni.toString());
                firestore.collection("users").document(emailString).set(newAlumni);
                break;
        }
    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Toast.makeText(this,"No location found, try again!",Toast.LENGTH_SHORT).show();
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d("GEOLOCATE", "geoLocate: found a location: " + address.toString());
            nextButton.setEnabled(true);
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
        else{
            Toast.makeText(this,"No location found, try again!",Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        }else{
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Toast.makeText(MapActivity.this,"Security error, please fix",Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        longitude = latLng.longitude;
        lat = latLng.latitude;

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Toast.makeText(MapActivity.this, "Select your address", Toast.LENGTH_LONG).show();
                mMap = googleMap;

                if (mLocationPermissionsGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    init();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void getLocationPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                Log.d("path","getLocationPermission go to initmap");
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}