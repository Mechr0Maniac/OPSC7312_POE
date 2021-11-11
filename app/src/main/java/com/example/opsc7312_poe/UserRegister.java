package com.example.opsc7312_poe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserRegister extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        email = findViewById(R.id.edtRegEmail);
        pass = findViewById(R.id.edtRegPass);
        regButton = findViewById(R.id.btnRegister);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailReg = email.getText().toString();
                String passReg = pass.getText().toString();

                RegisterNew(emailReg, passReg);
            }
        });
    }

    private void RegisterNew(String email, String password){

    }
}