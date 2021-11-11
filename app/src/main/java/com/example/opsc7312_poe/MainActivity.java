package com.example.opsc7312_poe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button addCat = findViewById(R.id.btnEnter);
        Button newRegister = findViewById(R.id.btnNewRegister);
        EditText user = findViewById(R.id.edtUser);
        EditText pass = findViewById(R.id.edtPass);

        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUser = user.getText().toString();
                String sPass = pass.getText().toString();

                if (sUser.equals("User") && sPass.equals("12345")){
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                } else if (user.equals("") && pass.equals("")){
                    Toast.makeText(MainActivity.this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
                } else if (user.equals("")){
                    Toast.makeText(MainActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (pass.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Username or password not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        newRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(MainActivity.this, UserRegister.class);
                startActivity(reg);
            }
        });
    }
}