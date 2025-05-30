package com.cs.restaurantfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                User.UserType selectedType;
                if (rbStaff.isChecked()) {
                    selectedType = User.UserType.STAFF;
                } else {
                    selectedType = User.UserType.CUSTOMER;
                }

                // Run the login in background
                new AsyncTask<Void, Void, User>() {
                    @Override
                    protected User doInBackground(Void... voids) {
                        return dbHelper.authenticateUser(username, password);
                    }

                    @Override
                    protected void onPostExecute(User user) {
                        if (user != null) {
                            // Save session
                            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username", user.getUsername());
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Intent intent;
                            if (selectedType == User.UserType.STAFF) {
                                intent = new Intent(MainActivity.this, StaffDashboardActivity.class);
                            } else {
                                intent = new Intent(MainActivity.this, CustomerDashboardActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private TextInputEditText etUsername, etPassword;
    private RadioGroup rgUserType;
    private RadioButton rbCustomer, rbStaff;
    private Button btnLogin;
    private DatabaseHelper dbHelper;
    private TextView txtRegister;

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        rgUserType = findViewById(R.id.rgUserType);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbStaff = findViewById(R.id.rbStaff);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
    }
}


