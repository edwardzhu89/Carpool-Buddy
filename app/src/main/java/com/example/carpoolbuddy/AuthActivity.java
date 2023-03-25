package com.example.carpoolbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.UUID;

public class AuthActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText emailEditText;
    private EditText passwordEditText;

    private EditText emailSignUpEditText;
    private EditText passwordSignUpEditText;
    private EditText nameEditText;

    private Spinner typeSpinner;
    private String type;

    private String param;
    private EditText paramEditText;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if(isServicesMet()){

            FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
            if(user2!= null){
                updateUI(user2, user2.getEmail());
            }

            mAuth = FirebaseAuth.getInstance();

            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);

            emailSignUpEditText = findViewById(R.id.emailSignUpEditText);
            passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);
            nameEditText = findViewById(R.id.nameEditText);

            typeSpinner = findViewById(R.id.typeSpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);
            typeSpinner.setOnItemSelectedListener(this);

            paramEditText=findViewById(R.id.paramEditText);
        }

    }

    public boolean isServicesMet(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS){
            // version met
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // can resolve issue
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            // cannot make map requests
            Toast.makeText(this, "Your Google API is outdated", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void signIn(View v){

        String emailString = emailEditText.getText().toString();
        String passwordString = passwordEditText.getText().toString();

        if(emailString.trim().length()<=0 || passwordString.trim().length()<=0){
            Toast.makeText(AuthActivity.this, "Missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("SIGN IN", "Successfully signed in user "+emailString);
                    updateUI(mAuth.getCurrentUser(), emailString);
                }
                else{
                    Log.d("SIGN IN", "Authentication Failed");
                    updateUI(null,"");
                }
            }
        });
    }

    public void signUpToMap(View v){

        String emailString = emailSignUpEditText.getText().toString();
        String passwordString = passwordSignUpEditText.getText().toString();
        String name = nameEditText.getText().toString();
        param = paramEditText.getText().toString();

        if(emailString.trim().length()<=0 || passwordString.trim().length()<=0 || name.trim().length()<=0 || (param.trim().length()<=0 && !Objects.equals(type, "Parent"))){
            Toast.makeText(AuthActivity.this, "Missing parameter", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!emailString.contains("@cis.edu.hk") && !emailString.contains("@alumni.cis.edu.hk")){
            Toast.makeText(AuthActivity.this, "Must be a cis.edu.hk email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(passwordSignUpEditText.getText().toString().length() < 8){
            Toast.makeText(AuthActivity.this, "Password must be at least 8 letters long", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(AuthActivity.this, MapActivity.class);

        Bundle b = new Bundle();
        b.putString("emailString", emailString);
        b.putString("passwordString",passwordString);
        b.putString("name",name);
        b.putString("param",param);
        b.putString("type",type);

        intent.putExtras(b);

        startActivity(intent);

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
                            Toast.makeText(AuthActivity.this, "Welcome "+user.getName(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        type=text;
        switch (text) {
            case "Teacher":
                paramEditText.setVisibility(View.VISIBLE);
                paramEditText.setHint("In school title");
                break;
            case "Alumni":
                paramEditText.setVisibility(View.VISIBLE);
                paramEditText.setHint("Graduating Year");
                break;
            case "Parent":
                paramEditText.setVisibility(View.INVISIBLE);
                break;
            default:
                paramEditText.setVisibility(View.VISIBLE);
                paramEditText.setHint("Graduating Year");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
//        if(user2!= null){
//            updateUI(user2, user2.getEmail());
//        }
//    }

}