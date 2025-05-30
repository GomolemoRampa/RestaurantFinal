package com.cs.restaurantfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Register extends AppCompatActivity {

    private TextInputEditText etFullName, etUsername, etEmail, etPhone, etPassword;
    private MaterialButton btnRegister;
    private MaterialRadioButton rbCustomer, rbStaff;
    private RadioGroup rgUserType;
    private CircularProgressIndicator progressBar;
    private TextView tvLoginLink;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        etFullName = findViewById(R.id.et_full_name);
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);
        rgUserType = findViewById(R.id.rg_user_type);
        rbCustomer = findViewById(R.id.rb_customer);
        rbStaff = findViewById(R.id.rb_staff);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        tvLoginLink = findViewById(R.id.tv_login_link);

        dbHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener(view -> registerUser());

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

// ...

    private void registerUser() {
        // Collect values from inputs
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Determine selected user type
        User.UserType userType = rbCustomer.isChecked() ? User.UserType.CUSTOMER : User.UserType.STAFF;

        // Validate fields
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Check if username already exists (this might also do DB/network access)
            if (dbHelper.isUsernameTaken(username)) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Create user and attempt to add
            User user = new User(0, fullName, username, email, phone, password, userType);
            boolean result = dbHelper.addUser(user);  // This should perform network I/O if you're using PHP/MySQL backend

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                if (result) {
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}
