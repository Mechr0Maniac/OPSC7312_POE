package com.example.opsc7312_poe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegister extends AppCompatActivity implements View.OnClickListener {

    private TextView btnRegister,banner;
    private EditText editRegEmail, editRegPass,edtRegName;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mAuth =FirebaseAuth.getInstance();

        banner =(TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        btnRegister =(Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        editRegEmail=(EditText) findViewById(R.id.edtUser);
        editRegPass=(EditText) findViewById(R.id.edtPass);
        edtRegName=(EditText) findViewById(R.id.edtRegName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void RegisterNew(String email, String password){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                registerUser();
                break;
        }
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
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user =new User(name,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserRegister.this, "User has been Registered succesfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(UserRegister.this, "Failed to register, please try again", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(UserRegister.this, "Failed to register, please try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}