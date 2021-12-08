package com.example.opsc7312_poe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRegister extends AppCompatActivity implements View.OnClickListener {

    private TextView btnRegister;
    private EditText editRegEmail, editRegPass,edtRegName;
    private ProgressBar progressBar;
    private ImageView btnBack;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mAuth =FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        btnBack=(ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        btnRegister =(Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        editRegEmail=(EditText) findViewById(R.id.edtRegEmail);
        editRegPass=(EditText) findViewById(R.id.edtRegPass);
        edtRegName=(EditText) findViewById(R.id.edtRegName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnBack:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }
    }

    private void successfulEnter(){
        startActivity( new Intent(this, MainActivity.class));
    }

    private void registerUser(){
        String email=editRegEmail.getText().toString().trim();
        String password= editRegPass.getText().toString().trim();
        String name=edtRegName.getText().toString().trim();

        if(name.isEmpty()){
            edtRegName.setError(("Name is required"));
            edtRegName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editRegEmail.setError("Email is required");
            editRegEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editRegEmail.setError("Please provide a valid email");
            editRegEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editRegPass.setError("Password is required");
            editRegPass.requestFocus();
            return;
        }

        if(password.length()<6){
            editRegPass.setError("Min password length should be 6 characters");
            editRegPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user =new User(name,email);

                            /*FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Toast.makeText(UserRegister.this, "User has been Registered successfully", Toast.LENGTH_LONG).show();
                                        successfulEnter();
                                    }else{
                                        Toast.makeText(UserRegister.this, "Failed to register user, please try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });*/
                            //databaseReference.child("email").setValue("name");
                            databaseReference.child("Users").child(email).setValue(name);
                        }else{
                            Toast.makeText(UserRegister.this, "Failed to register, please try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}